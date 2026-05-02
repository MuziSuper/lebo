package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.CategoryListDTO;
import cn.muzisheng.lebo.entity.Category;
import cn.muzisheng.lebo.exception.CategoryException;
import cn.muzisheng.lebo.mapper.CategoryMapper;
import cn.muzisheng.lebo.service.HistoryOperationService;
import cn.muzisheng.lebo.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Category tests cover the business rules that protect product taxonomy:
 * duplicate names are rejected, categories with products cannot be deleted, and
 * successful mutations write history-operation records.
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private ProductService productService;
    @Mock
    private HistoryOperationService historyOperationService;
    @Mock
    private CategoryMapper categoryMapper;

    private CategoryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CategoryServiceImpl(productService, historyOperationService);
        ReflectionTestUtils.setField(service, "baseMapper", categoryMapper);
    }

    @Test
    void createTrimsNameRejectsDuplicatesAndAddsHistory() {
        when(categoryMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);
        when(categoryMapper.insert(any(Category.class))).thenReturn(1);

        assertThat(service.create(" 饮品 ").getBody().getData()).isTrue();
        verify(historyOperationService).addHistoryOperation(6, "添加分类：饮品");

        Category existing = category(1L, "饮品");
        when(categoryMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(existing);
        assertThatThrownBy(() -> service.create("饮品"))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类名称已存在");
        assertThatThrownBy(() -> service.create(" "))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类名称不能为空");
    }

    @Test
    void createReportsFailedInsertAfterDuplicateCheckPasses() {
        when(categoryMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);
        when(categoryMapper.insert(any(Category.class))).thenReturn(0);

        assertThatThrownBy(() -> service.create("饮品"))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("创建分类失败");
    }

    @Test
    void updateValidatesInputDuplicatesAndPersistsExistingCategory() {
        Category existing = category(1L, "旧名称");
        Category request = category(1L, " 新名称 ");
        when(categoryMapper.selectById(1L)).thenReturn(existing);
        when(categoryMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);
        when(categoryMapper.updateById(existing)).thenReturn(1);

        assertThat(service.update(request).getBody().getData()).isTrue();
        assertThat(existing.getName()).isEqualTo("新名称");
        verify(historyOperationService).addHistoryOperation(7, "修改分类：新名称");

        assertThatThrownBy(() -> service.update((Category) null))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类 ID 不能为空");
        assertThatThrownBy(() -> service.update(category(2L, " ")))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类名称不能为空");
        when(categoryMapper.selectById(99L)).thenReturn(null);
        assertThatThrownBy(() -> service.update(category(99L, "不存在")))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类不存在");
    }

    @Test
    void updateRejectsDuplicateNameAndFailedPersistence() {
        Category existing = category(1L, "旧名称");
        when(categoryMapper.selectById(1L)).thenReturn(existing);
        when(categoryMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(category(2L, "新名称"));

        assertThatThrownBy(() -> service.update(category(1L, "新名称")))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类名称已存在");

        when(categoryMapper.selectOne(any(Wrapper.class), eq(true))).thenReturn(null);
        when(categoryMapper.updateById(existing)).thenReturn(0);
        assertThatThrownBy(() -> service.update(category(1L, "另一个名称")))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("更新分类失败");
    }

    @Test
    void deleteRejectsMissingCategoryAndCategoryContainingProducts() {
        Category existing = category(1L, "饮品");
        when(categoryMapper.selectById(1L)).thenReturn(existing);
        when(productService.existByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类下存在商品");

        when(productService.existByCategoryId(1L)).thenReturn(false);
        when(categoryMapper.deleteById(1L)).thenReturn(1);
        assertThat(service.delete(1L).getBody().getData()).isTrue();
        verify(historyOperationService).addHistoryOperation(8, "删除分类：饮品");

        assertThatThrownBy(() -> service.delete(null))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类 ID 不能为空");
        when(categoryMapper.selectById(2L)).thenReturn(null);
        assertThatThrownBy(() -> service.delete(2L))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类不存在");
    }

    @Test
    void deleteReportsFailedMapperDelete() {
        when(categoryMapper.selectById(3L)).thenReturn(category(3L, "零食"));
        when(productService.existByCategoryId(3L)).thenReturn(false);
        when(categoryMapper.deleteById(3L)).thenReturn(0);

        assertThatThrownBy(() -> service.delete(3L))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("删除分类失败");
    }

    @Test
    void categoryListSupportsAllDataAndPagedQueries() {
        when(categoryMapper.selectList(any(Wrapper.class))).thenReturn(List.of(category(1L, "A"), category(2L, "B")));
        assertThat(service.categoryList(null).getBody().getData().getRecords())
                .extracting(Category::getName)
                .containsExactly("A", "B");

        CategoryListDTO dto = new CategoryListDTO();
        dto.setName("饮");
        dto.setPageNum(1);
        dto.setPageSize(10);
        when(categoryMapper.selectPage(any(Page.class), any(Wrapper.class)))
                .thenAnswer(invocation -> {
                    Page<Category> page = invocation.getArgument(0);
                    page.setRecords(List.of(category(3L, "饮品")));
                    page.setTotal(1);
                    return page;
                });

        assertThat(service.categoryList(dto).getBody().getData().getRecords())
                .extracting(Category::getName)
                .containsExactly("饮品");
    }

    private Category category(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}
