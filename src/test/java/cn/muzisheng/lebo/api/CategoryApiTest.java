package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.CategoryListDTO;
import cn.muzisheng.lebo.entity.Category;
import cn.muzisheng.lebo.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class CategoryApiTest extends UnitTestSupport {

    @Mock
    private CategoryService categoryService;

    @Test
    void delegatesAllCategoryEndpoints() {
        CategoryApi api = new CategoryApi(categoryService);
        Category category = new Category();
        CategoryListDTO listDTO = new CategoryListDTO();
        api.create("饮品");
        api.update(category);
        api.delete(1L);
        api.list(listDTO);

        verify(categoryService).create("饮品");
        verify(categoryService).update(category);
        verify(categoryService).delete(1L);
        verify(categoryService).categoryList(listDTO);
    }
}
