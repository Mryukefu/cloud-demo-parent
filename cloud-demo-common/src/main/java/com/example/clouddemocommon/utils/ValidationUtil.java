package com.example.clouddemocommon.utils;
import com.example.clouddemocommon.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.NumberUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yukefu
 * @Title ValidationUtil
 * @ProjectName jkb-plat-backend
 * @Description: 验证处理工具类
 * @Date 2018/6/29 21:17
 */
public class ValidationUtil {
    //private static final SystemCodeEnum ERROR_ENUM = SystemCodeEnum.PARAM_ERROR;

    /**
     * 处理校验异常信息:若存在多个异常,则以以下格式提示
     * 信息1|信息2|信息3
     *@Author yukefu
     * @param bindingResult
     */
    public static void handleBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            String errorMessage = fieldErrors.stream().
                    map(FieldError::getDefaultMessage).
                    collect(Collectors.joining("|"));
            throwParamException(errorMessage);
        }
    }

    /**
     * 校验 value 是否 在 assertValues 内
     *@Author yukefu
     * @param tip          错误提示
     * @param value        判断值
     * @param assertValues 校验值
     * @param <T>
     */
    public static <T> void assertIn(String tip, T value, T... assertValues) {
        if (assertValues == null) {
            assertTrue(value == null, tip);
            return;
        }

        if (!Arrays.stream(assertValues).collect(Collectors.toList()).contains(value)) {
            throwParamException(tip);
        }

    }

    /**
     * 校验是否为空
     *@Author yukefu
     * @param value
     * @param tip
     */
    public static void assertNotBlank(String value, String tip) {
        if (!StringUtils.isNotBlank(value)) {
            throwParamException(tip);
        }
    }

    /**
     * 抛出一个参数校验异常
     *
     * @param tip
     */
    private static void throwParamException(String tip) {
        throw new BusinessException(tip,300);
    }

    /**
     * 校验最小取值
     *@Author yukefu
     * @param val
     * @param assertVal
     * @param tip
     */
    public static void assertMinValue(Number val, Number assertVal, String tip, Object... param) {
        assertNotNull(val, tip, param);
        assertNotNull(assertVal, tip, param);
        BigDecimal one = NumberUtils.convertNumberToTargetClass(val, BigDecimal.class);
        BigDecimal another = NumberUtils.convertNumberToTargetClass(assertVal, BigDecimal.class);

        if (one.compareTo(another) < 0) {
            throwParamException(String.format(tip, param));
        }
    }

    /**
     * 校验最小(含等于)取值
     *@Author yukefu
     * @param val
     * @param assertVal
     * @param tip
     */
    public static void assertMinEqValue(Number val, Number assertVal, String tip) {
        assertNotNull(val, tip);
        assertNotNull(assertVal, tip);
        BigDecimal one = NumberUtils.convertNumberToTargetClass(val, BigDecimal.class);
        BigDecimal another = NumberUtils.convertNumberToTargetClass(assertVal, BigDecimal.class);

        if (one.compareTo(another) <= 0) {
            throwParamException(tip);
        }
    }

    /**
     * 校验最大(含等于)取值
     *@Author yukefu
     * @param val
     * @param assertVal
     * @param tip
     */
    public static void assertMaxEqValue(Number val, Number assertVal, String tip) {
        assertNotNull(val, tip);
        assertNotNull(assertVal, tip);
        BigDecimal one = NumberUtils.convertNumberToTargetClass(val, BigDecimal.class);
        BigDecimal another = NumberUtils.convertNumberToTargetClass(assertVal, BigDecimal.class);

        if (one.compareTo(another) >= 0) {
            throwParamException(tip);
        }
    }

    /**
     * 校验最大取值
     *
     * @param val
     * @param assertVal
     * @param tip
     */
    public static void assertMaxValue(Number val, Number assertVal, String tip) {
        assertNotNull(val, tip);
        assertNotNull(assertVal, tip);
        BigDecimal one = NumberUtils.convertNumberToTargetClass(val, BigDecimal.class);
        BigDecimal another = NumberUtils.convertNumberToTargetClass(assertVal, BigDecimal.class);

        if (one.compareTo(another) > 0) {
            throwParamException(tip);
        }
    }

    /**
     * 校验值不等 值相等抛出异常
     *@Author yukefu
     * @param val
     * @param assertVal
     * @param tip
     */
    public static void assertNotEqValue(Number val, Number assertVal, String tip, Object... params) {
        assertNotNull(val, tip);
        assertNotNull(assertVal, tip);
        BigDecimal one = NumberUtils.convertNumberToTargetClass(val, BigDecimal.class);
        BigDecimal another = NumberUtils.convertNumberToTargetClass(assertVal, BigDecimal.class);

        if (one.compareTo(another) == 0) {
            throwParamException(String.format(tip, params));
        }
    }

    /**
     * 校验值相等 值不等抛出异常
     *@Author yukefu
     * @param val
     * @param assertVal
     * @param tip
     */
    public static void assertEqValue(Number val, Number assertVal, String tip) {
        assertNotNull(val, tip);
        assertNotNull(assertVal, tip);
        BigDecimal one = NumberUtils.convertNumberToTargetClass(val, BigDecimal.class);
        BigDecimal another = NumberUtils.convertNumberToTargetClass(assertVal, BigDecimal.class);

        if (one.compareTo(another) != 0) {
            throwParamException(tip);
        }
    }

    /**
     * 校验取值是否在某范围
     *@Author yukefu
     * @param val
     * @param assertMinVal
     * @param assertMaxVal
     * @param tip
     */
    public static void assertRangeValue(Integer val, int assertMinVal, int assertMaxVal, String tip) {
        if (val == null || val < assertMinVal || val > assertMaxVal) {
            throwParamException(tip);
        }
    }

    /**
     * 判断集合是否为空（没有内容）
     *@Author yukefu
     * @param list   需要判断的集合
     * @param tip    提示内容
     * @param params 填充tip的参数
     * @return void
     * @author ylb
     * @date 2019/6/5 9:39
     */
    public static void assertListEquals(List list, String tip, String... params) {
        if (list == null || list.size() <= 0) {
            throwParamException(String.format(tip, params));
        }
    }

    /**
     * 校验字符串长度
     *@Author yukefu
     * @param val
     * @param length
     * @param tip
     */
    public static void assertLength(String val, int length, String tip) {
        if (val == null || val.trim().length() > length) {
            throwParamException(tip);
        }
    }

    /**
     * 校验数字类型ID
     *@Author yukefu
     * @param obj
     * @param tip
     */
    public static void assertNumericalId(Object obj, String tip) {
        if (obj != null) {
            try {
                long num = Long.valueOf(obj.toString());
                if (num > 0) {
                    return;
                }
            } catch (Exception e) {
            }
        }
        throwParamException(tip);
    }

    /**
     * 校验a是否大于b
     *@Author yukefu
     * @param small 较小数
     * @param large 较大数
     * @param tip   提示
     */
    public static void assertALessB(Object small, Object large, String tip) {
        if (small != null && large != null) {
            try {
                long lSmall = Long.valueOf(small.toString());
                long lLarge = Long.valueOf(large.toString());
                if (lSmall < lLarge) {
                    return;
                }
            } catch (Exception e) {
            }
        }
        throwParamException(tip);
    }

    /**
     * 检查对象是否为null
     *@Author yukefu
     * @param object
     * @param tip
     */
    public static void assertNotNull(Object object, String tip, Object... params) {
        if (object == null) {
            throwParamException(String.format(tip, params));
        }
    }

    /**
     * 校验要比较的对象是否相等
     * 若为原始类型或者封装类型
     *@Author yukefu
     * @param tip     错误提示
     * @param objects 要比较的对象
     */
    public static void assertNotEquals(String tip, Object... objects) {
        assertEqualsOrNotEquals(false, tip, objects);
    }

    /**
     * 校验要比较的对象是否相等
     * 若为原始类型或者封装类型
     *
     * @param tip     错误提示
     * @param objects 要比较的对象
     */
    public static void assertEquals(String tip, Object... objects) {
        assertEqualsOrNotEquals(true, tip, objects);
    }


    private static void assertEqualsOrNotEquals(boolean isEqualsOperate, String tip, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            for (int j = i + 1; j < objects.length; j++) {
                Object obj1 = objects[i];
                Object obj2 = objects[j];
                if (judgeType(obj1) && judgeType(obj2)) { // 数字比较
                    boolean match = isEqualsOperate ? Double.valueOf(obj1.toString()).compareTo(Double.valueOf(obj2.toString())) != 0 :
                            Double.valueOf(obj1.toString()).compareTo(Double.valueOf(obj2.toString())) == 0;
                    if (match) {
                        throwParamException(tip);
                    }
                } else {
                    boolean match = isEqualsOperate != obj1.equals(obj2);
                    if (match) { // 对象比较
                        throwParamException(tip);
                    }
                }
            }
        }
    }

    /**
     * 判断是否为数字类型
     *@Author yukefu
     * @param obj
     * @return
     */
    private static boolean judgeType(Object obj) {
        return obj instanceof Number;
    }

    /**
     * 校验是否为true,若为false则抛出校验异常
     *@Author yukefu
     * @param predict
     * @param tip
     */
    public static void assertTrue(boolean predict, String tip, Object... params) {
        if (!predict) {
            throwParamException(String.format(tip, params));
        }
    }

    /**
     * 校验是否false 若为true则抛出校验异常
     *@Author yukefu
     * @param predicate
     * @param tip
     */
    public static void assertFalse(boolean predicate, String tip, Object... params) {
        if (predicate) {
            throwParamException(String.format(tip, params));
        }
    }

    /**
     * 若obj不为空则抛异常
     *@Author yukefu
     * @param obj
     * @param tip
     */
    public static void assertNull(Object obj, String tip) {
        if (obj != null) {
            throwParamException(tip);
        }
    }


}

