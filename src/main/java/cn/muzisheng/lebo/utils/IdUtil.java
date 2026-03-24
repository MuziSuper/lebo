package cn.muzisheng.lebo.utils;

import cn.muzisheng.lebo.exception.GeneralException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成工具类
 * 每种类型的ID拥有独立的序列号计数器
 */
public class IdUtil {

    /** 订单ID前缀 */
    private static final String ORDER_ID_PREFIX = "ORD_";

    /** 图片ID前缀 */
    private static final String IMAGE_ID_PREFIX = "IMAGE_";

    /** 商品ID前缀 */
    private static final String PRODUCT_ID_PREFIX = "PRO_";

    /** 用户积分ID前缀 */
    private static final String USER_POINT_ID_PREFIX = "UP_";

    /** 历史操作记录ID前缀 */
    private static final String HISTORY_OPERATION_ID_PREFIX = "HIS_";

    /** 出入库记录ID前缀 */
    private static final String IN_OUT_RECORD_ID_PREFIX = "INOUT_";

    /** 积分记录ID前缀 */
    private static final String POINT_RECORD_ID_PREFIX = "PR_";

    /** 日期时间格式化器 */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** 序列号最大值（每秒最多99个） */
    private static final int MAX_SEQUENCE = 99;

    /** 每个前缀对应的序列号计数器 */
    private static final Map<String, AtomicInteger> SEQUENCE_MAP = new ConcurrentHashMap<>();

    /** 每个前缀对应的当前秒时间戳 */
    private static final Map<String, String> CURRENT_SECOND_MAP = new ConcurrentHashMap<>();

    static {
        SEQUENCE_MAP.put(ORDER_ID_PREFIX, new AtomicInteger(0));
        SEQUENCE_MAP.put(IMAGE_ID_PREFIX, new AtomicInteger(0));
        SEQUENCE_MAP.put(PRODUCT_ID_PREFIX, new AtomicInteger(0));
        SEQUENCE_MAP.put(USER_POINT_ID_PREFIX, new AtomicInteger(0));
        SEQUENCE_MAP.put(HISTORY_OPERATION_ID_PREFIX, new AtomicInteger(0));
        SEQUENCE_MAP.put(IN_OUT_RECORD_ID_PREFIX, new AtomicInteger(0));
        SEQUENCE_MAP.put(POINT_RECORD_ID_PREFIX, new AtomicInteger(0));

        CURRENT_SECOND_MAP.put(ORDER_ID_PREFIX, "");
        CURRENT_SECOND_MAP.put(IMAGE_ID_PREFIX, "");
        CURRENT_SECOND_MAP.put(PRODUCT_ID_PREFIX, "");
        CURRENT_SECOND_MAP.put(USER_POINT_ID_PREFIX, "");
        CURRENT_SECOND_MAP.put(HISTORY_OPERATION_ID_PREFIX, "");
        CURRENT_SECOND_MAP.put(IN_OUT_RECORD_ID_PREFIX, "");
        CURRENT_SECOND_MAP.put(POINT_RECORD_ID_PREFIX, "");
    }

    /**
     * 生成32位的UUID
     * @return 32位UUID字符串
     */
    public static String generateId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

    /**
     * 生成订单ID
     * 格式：ORD_yyyyMMddHHmmss + 2位序列号 + 2位随机数
     * 示例：ORD_202506021103020153
     * 线程安全，每秒最多生成99个订单ID
     *
     * @return 订单ID
     */
    public static synchronized String generateOrderId() {
        return generate(ORDER_ID_PREFIX);
    }

    /**
     * 生成用户积分ID
     * 格式：UP_yyyyMMddHHmmss + 2位序列号 + 2位随机数
     * 示例：UP_202506021103020153
     * 线程安全，每秒最多生成99个用户积分ID
     *
     * @return 用户积分ID
     */
    public static synchronized String generateUserPointId() {
        return generate(USER_POINT_ID_PREFIX);
    }

    /**
     * 生成商品ID
     * 格式：PRO_yyyyMMddHHmmss + 2位序列号 + 2位随机数
     * 示例：PRO_202506021103020153
     * 线程安全，每秒最多生成99个商品ID
     *
     * @return 商品ID
     */
    public static synchronized String generateProductId() {
        return generate(PRODUCT_ID_PREFIX);
    }

    /**
     * 生成图片ID
     * 格式：IMAGE_yyyyMMddHHmmss + 2位序列号 + 2位随机数
     * 示例：IMAGE_202506021103020153
     * 线程安全，每秒最多生成99个图片ID
     *
     * @return 图片ID
     */
    public static synchronized String generateImageId() {
        return generate(IMAGE_ID_PREFIX);
    }

    /**
     * 生成历史操作记录ID
     * 格式：HIS_yyyyMMddHHmmss + 2位序列号 + 2位随机数
     * 示例：HIS_202506021103020153
     * 线程安全，每秒最多生成99个历史操作记录ID
     *
     * @return 历史操作记录ID
     */
    public static synchronized String generateHistoryOperationId() {
        return generate(HISTORY_OPERATION_ID_PREFIX);
    }

    /**
     * 生成出入库记录ID
     * 格式：INOUT_yyyyMMddHHmmss + 2位序列号 + 2位随机数
     * 示例：INOUT_202506021103020153
     * 线程安全，每秒最多生成99个出入库记录ID
     *
     * @return 出入库记录ID
     */
    public static synchronized String generateInOutRecordId() {
        return generate(IN_OUT_RECORD_ID_PREFIX);
    }

    /**
     * 生成积分记录ID
     * 格式：PR_yyyyMMddHHmmss + 2位序列号 + 2位随机数
     * 示例：PR_202506021103020153
     * 线程安全，每秒最多生成99个积分记录ID
     *
     * @return 积分记录ID
     */
    public static synchronized String generatePointRecordId() {
        return generate(POINT_RECORD_ID_PREFIX);
    }

    /**
     * 生成指定前缀的ID
     * 每个前缀拥有独立的序列号计数器
     * 格式：前缀 + 时间戳 + 2位序列号 + 2位随机数
     *
     * @param prefix ID前缀
     * @return 带前缀的ID
     */
    private static String generate(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DATE_TIME_FORMATTER);

        AtomicInteger sequence = SEQUENCE_MAP.get(prefix);
        String currentSecond = CURRENT_SECOND_MAP.get(prefix);

        if (!timestamp.equals(currentSecond)) {
            CURRENT_SECOND_MAP.put(prefix, timestamp);
            sequence.set(0);
        }

        int seq = sequence.incrementAndGet();
        if (seq > MAX_SEQUENCE) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new GeneralException("ID生成异常", e);
            }
            return generate(prefix);
        }

        int random = ThreadLocalRandom.current().nextInt(0, 100);

        return prefix + timestamp + String.format("%02d", seq) + String.format("%02d", random);
    }

    /**
     * 生成唯一文件名
     * @param file 上传的文件
     * @return 唯一的文件名
     */
    public static String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        String imageName = generate(IMAGE_ID_PREFIX);

        // 增强文件名处理
        if (originalFilename != null && !originalFilename.isEmpty()) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
        }

        return imageName + extension;
    }
}
