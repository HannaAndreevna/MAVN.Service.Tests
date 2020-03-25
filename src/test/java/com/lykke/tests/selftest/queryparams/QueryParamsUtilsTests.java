package com.lykke.tests.selftest.queryparams;

import static com.lykke.api.testing.api.common.QueryParamsUtils.getQueryParams;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lykke.tests.selftest.queryparams.model.a.ObjectListRequest;
import com.lykke.tests.selftest.queryparams.model.a.PagedRequestModel;
import com.lykke.tests.selftest.queryparams.model.b.EventListRequest;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class QueryParamsUtilsTests {

    private static final String STRING_DATA_FIELD = "StringData";
    private static final String STRING_DATA_1_FIELD = "StringData1";
    private static final String STRING_DATA_2_FIELD = "StringData2";
    private static final String STRING_DATA_3_FIELD = "StringData3";
    private static final String INT_DATA_FIELD = "IntData";
    private static final String FLOAT_DATA_FIELD = "FloatData";
    private static final String DOUBLE_DATA_FIELD = "DoubleData";
    private static final String STRING_ARRAY_DATA_FIELD = "StringArrayData";
    private static final String INT_ARRAY_DATA_FIELD = "IntArrayData";
    private static final String FLOAT_ARRAY_DATA_FIELD = "FloatArrayData";
    private static final String DOUBLE_ARRAY_DATA_FIELD = "DoubleArrayData";
    private static final String STRING_DATA_SUBCLASS_FIELD = "StringDataSubClass";
    private static final String INT_DATA_SUBCLASS_FIELD = "IntDataSubClass";
    private static final String FLOAT_DATA_SUBCLASS_FIELD = "FloatDataSubClass";
    private static final String DOUBLE_DATA_SUBCLASS_FIELD = "DoubleDataSubClass";

    private static final String SOME_STRING_VALUE_01 = "aaa.bbb.ccc";
    private static final String SOME_STRING_VALUE_02 = "sadf\n;ljk\rs;ldfjk\\w";
    private static final String SOME_STRING_VALUE_03 = "\"";
    private static final int SOME_INT_VALUE_01 = 3;
    private static final Integer SOME_INT_VALUE_02 = -4;
    private static final int SOME_INT_VALUE_03 = 101;
    private static final float SOME_FLOAT_VALUE_01 = 5;
    private static final Float SOME_FLOAT_VALUE_02 = -6F;
    private static final double SOME_DOUBLE_VALUE_01 = 7.1;
    private static final Double SOME_DOUBLE_VALUE_02 = -18.1;
    private static final String EXPECTED_STRING_VALUE_01 = SOME_STRING_VALUE_01;
    private static final String EXPECTED_STRING_VALUE_02 = SOME_STRING_VALUE_02;
    private static final String EXPECTED_STRING_VALUE_03 = SOME_STRING_VALUE_03;
    private static final String EXPECTED_INT_VALUE_01 = String.valueOf(SOME_INT_VALUE_01);
    private static final String EXPECTED_INT_VALUE_02 = String.valueOf(SOME_INT_VALUE_02);
    private static final String EXPECTED_INT_VALUE_03 = String.valueOf(SOME_INT_VALUE_03);
    private static final String EXPECTED_FLOAT_VALUE_01 = String.valueOf(SOME_FLOAT_VALUE_01);
    private static final String EXPECTED_FLOAT_VALUE_02 = String.valueOf(SOME_FLOAT_VALUE_02);
    private static final String EXPECTED_DOUBLE_VALUE_01 = String.valueOf(SOME_DOUBLE_VALUE_01);
    private static final String EXPECTED_DOUBLE_VALUE_02 = String.valueOf(SOME_DOUBLE_VALUE_02);
    private static final String ZERO_INT_IGNORED = "0";
    private static final String ZERO_DOUBLE_IGNORED = "0.0";

    @Test
    void shouldNotExtractParametersOnEmptyClass() {
        val actualResult = getQueryParams(EmptyClass.builder().build());

        assertEquals(0, actualResult.entrySet().size());
    }

    @Test
    void shouldNotExtractParametersOnClassWithEmptyStringFields() {
        val actualResult = getQueryParams(OnlyStringClass.builder().build());

        assertEquals(0, actualResult.entrySet().size());
    }

    @Test
    void shouldNotExtractParametersOnClassWithEmptyFields() {
        val actualResult = getQueryParams(SimpleClass.builder().build(),
                x -> !EMPTY.equalsIgnoreCase(x)
                        && !ZERO_DOUBLE_IGNORED.equalsIgnoreCase(x)
                        && !ZERO_INT_IGNORED.equalsIgnoreCase(x));

        assertEquals(0, actualResult.entrySet().size());
    }

    @Test
    void shouldNotExtractParametersOnSpecificIgoreSettings() {
        val actualResult = getQueryParams(SimpleClass
                        .builder()
                        .stringData(SOME_STRING_VALUE_01)
                        .intData(SOME_INT_VALUE_01)
                        .floatData(SOME_FLOAT_VALUE_01)
                        .doubleData(SOME_DOUBLE_VALUE_01)
                        .build(),
                x -> !EXPECTED_STRING_VALUE_01.equalsIgnoreCase(x)
                        && !EXPECTED_DOUBLE_VALUE_01.equalsIgnoreCase(x)
                        && !EXPECTED_FLOAT_VALUE_01.equalsIgnoreCase(x)
                        && !EXPECTED_INT_VALUE_01.equalsIgnoreCase(x));

        assertEquals(0, actualResult.entrySet().size());
    }

    @Test
    void shouldExtractParametersOnClassWithStringFields() {
        val actualResult = getQueryParams(OnlyStringClass
                .builder()
                .stringData1(SOME_STRING_VALUE_01)
                .stringData2(SOME_STRING_VALUE_02)
                .stringData3(SOME_STRING_VALUE_03)
                .build());

        assertAll(
                () -> assertEquals(Stream.of(
                        EXPECTED_STRING_VALUE_01,
                        EXPECTED_STRING_VALUE_02,
                        EXPECTED_STRING_VALUE_03).count(), actualResult.entrySet().size()),
                () -> assertTrue(actualResult.keySet().containsAll(
                        Stream.of(
                                STRING_DATA_1_FIELD,
                                STRING_DATA_2_FIELD,
                                STRING_DATA_3_FIELD)
                                .collect(toList()))),
                () -> assertTrue(actualResult.values().containsAll(
                        Stream.of(
                                EXPECTED_STRING_VALUE_01,
                                EXPECTED_STRING_VALUE_02,
                                EXPECTED_STRING_VALUE_03)
                                .collect(toList())))
        );
    }

    @Test
    void shouldExtractParametersOnClassWithVariousFields() {
        val actualResult = getQueryParams(SimpleClass
                        .builder()
                        .stringData(SOME_STRING_VALUE_01)
                        .intData(SOME_INT_VALUE_01)
                        .floatData(SOME_FLOAT_VALUE_01)
                        .doubleData(SOME_DOUBLE_VALUE_01)
                        .build(),
                x -> !EMPTY.equalsIgnoreCase(x)
                        && !ZERO_DOUBLE_IGNORED.equalsIgnoreCase(x)
                        && !ZERO_INT_IGNORED.equalsIgnoreCase(x));

        assertAll(
                () -> assertEquals(
                        Stream.of(
                                EXPECTED_STRING_VALUE_01,
                                EXPECTED_INT_VALUE_01,
                                EXPECTED_FLOAT_VALUE_01,
                                EXPECTED_DOUBLE_VALUE_01).count(),
                        actualResult.entrySet().size()),
                () -> assertTrue(actualResult.keySet().containsAll(
                        Stream.of(
                                STRING_DATA_FIELD,
                                INT_DATA_FIELD,
                                FLOAT_DATA_FIELD,
                                DOUBLE_DATA_FIELD)
                                .collect(toList()))),
                () -> assertTrue(actualResult.values().containsAll(
                        Stream.of(
                                EXPECTED_STRING_VALUE_01,
                                EXPECTED_INT_VALUE_01,
                                EXPECTED_FLOAT_VALUE_01,
                                EXPECTED_DOUBLE_VALUE_01)
                                .collect(toList())))
        );
    }

    @Test
    void shouldExtractParametersOnClassWithArrayFields() {
        val actualResult = getQueryParams(ClassWithArrays
                        .builder()
                        .stringData(SOME_STRING_VALUE_01)
                        .intData(SOME_INT_VALUE_01)
                        .floatData(SOME_FLOAT_VALUE_01)
                        .doubleData(SOME_DOUBLE_VALUE_01)
                        .stringArrayData(new String[]{SOME_STRING_VALUE_02})
                        .intArrayData(new Integer[]{SOME_INT_VALUE_02})
                        .floatArrayData(new Float[]{SOME_FLOAT_VALUE_02})
                        .doubleArrayData(new Double[]{SOME_DOUBLE_VALUE_02})
                        .build(),
                x -> !EMPTY.equalsIgnoreCase(x)
                        && !ZERO_DOUBLE_IGNORED.equalsIgnoreCase(x)
                        && !ZERO_INT_IGNORED.equalsIgnoreCase(x));

        assertAll(
                () -> assertEquals(
                        Stream.of(
                                EXPECTED_STRING_VALUE_01,
                                EXPECTED_INT_VALUE_01,
                                EXPECTED_FLOAT_VALUE_01,
                                EXPECTED_DOUBLE_VALUE_01,
                                EXPECTED_STRING_VALUE_02,
                                EXPECTED_INT_VALUE_02,
                                EXPECTED_FLOAT_VALUE_02,
                                EXPECTED_DOUBLE_VALUE_02
                        ).count(),
                        actualResult.entrySet().size()),
                () -> assertTrue(actualResult.keySet().containsAll(
                        Stream.of(
                                STRING_DATA_FIELD,
                                INT_DATA_FIELD,
                                FLOAT_DATA_FIELD,
                                DOUBLE_DATA_FIELD,
                                STRING_ARRAY_DATA_FIELD,
                                INT_ARRAY_DATA_FIELD,
                                FLOAT_ARRAY_DATA_FIELD,
                                DOUBLE_ARRAY_DATA_FIELD)
                                .collect(toList()))),
                () -> assertTrue(actualResult.values().containsAll(
                        Stream.of(
                                EXPECTED_STRING_VALUE_01,
                                EXPECTED_INT_VALUE_01,
                                EXPECTED_FLOAT_VALUE_01,
                                EXPECTED_DOUBLE_VALUE_01,
                                SOME_STRING_VALUE_02,
                                EXPECTED_INT_VALUE_02,
                                EXPECTED_FLOAT_VALUE_02,
                                EXPECTED_DOUBLE_VALUE_02)
                                .collect(toList())))
        );
    }

    @Disabled
    @Test
    void shouldExtractParametersOnSpecificIgoreSettings() {
        val actualResult = getQueryParams(SimpleClass
                        .builder()
                        .stringData(SOME_STRING_VALUE_01)
                        .intData(SOME_INT_VALUE_01)
                        .floatData(SOME_FLOAT_VALUE_01)
                        .doubleData(SOME_DOUBLE_VALUE_01)
                        .build(),
                x -> !EXPECTED_STRING_VALUE_01.equalsIgnoreCase(x)
                        && !EXPECTED_DOUBLE_VALUE_01.equalsIgnoreCase(x)
                        && !EXPECTED_FLOAT_VALUE_01.equalsIgnoreCase(x)
                        && !EXPECTED_INT_VALUE_01.equalsIgnoreCase(x));

        assertEquals(0, actualResult.entrySet().size());
    }

    @Test
    void shouldExtractParametersOnClassWithSubclassFields() {
        val complexFieldName = "SubClass2" + "." + STRING_DATA_SUBCLASS_FIELD;
        val pageSize = 500;
        val expectedEntry = new Entry<String, String>() {
            @Override
            public String getKey() {
                return complexFieldName;
            }

            @Override
            public String getValue() {
                return SOME_STRING_VALUE_02;
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };

        val actualResult = getQueryParams(ParentClass
                .builder()
                .stringData(SOME_STRING_VALUE_01)
                .intData(SOME_INT_VALUE_01)
                .floatData(SOME_FLOAT_VALUE_01)
                .doubleData(SOME_DOUBLE_VALUE_01)
                .subClass1(SubClass
                        .builder()
                        .build())
                .subClass2(SubClass
                        .builder()
                        .stringDataSubClass(SOME_STRING_VALUE_02)
                        .doubleDataSubClass(SOME_DOUBLE_VALUE_02)
                        .build())
                .pagedRequest(PagedRequestModel
                        .builder()
                        .pageSize(pageSize)
                        .build())
                .objectListRequest0(ObjectListRequest
                        .campaignBuilder()
                        .campaignName("name")
                        .currentPage(11)
                        .pageSize(22)
                        .build())
                .build());

        actualResult.entrySet().stream().forEach(item -> System.out.println(item.getKey() + ": " + item.getValue()));

        val actualEntry = actualResult.entrySet()
                .stream()
                .filter(entry -> entry.getKey()
                        .equals(complexFieldName))
                .findFirst()
                .orElse(new Entry<String, String>() {
                    @Override
                    public String getKey() {
                        return null;
                    }

                    @Override
                    public String getValue() {
                        return null;
                    }

                    @Override
                    public String setValue(String value) {
                        return null;
                    }
                });

        assertAll(
                () -> assertEquals(Stream.of(
                        EXPECTED_STRING_VALUE_01,
                        EXPECTED_INT_VALUE_01,
                        EXPECTED_FLOAT_VALUE_01,
                        EXPECTED_DOUBLE_VALUE_01,
                        EXPECTED_STRING_VALUE_02,
                        EXPECTED_DOUBLE_VALUE_02,
                        String.valueOf(pageSize),
                        "name",
                        String.valueOf(11),
                        String.valueOf(22)).count(), actualResult.entrySet().size()),
                () -> assertTrue(actualResult.keySet().containsAll(
                        Stream.of(
                                STRING_DATA_FIELD,
                                INT_DATA_FIELD,
                                FLOAT_DATA_FIELD,
                                DOUBLE_DATA_FIELD,
                                complexFieldName,
                                "SubClass2" + "." + DOUBLE_DATA_SUBCLASS_FIELD,
                                "PagedRequest.PageSize",
                                "ObjectListRequest0.CampaignName",
                                "ObjectListRequest0.CurrentPage",
                                "ObjectListRequest0.PageSize")
                                .collect(toList()))),
                () -> assertTrue(actualResult.values().containsAll(
                        Stream.of(
                                EXPECTED_STRING_VALUE_01,
                                EXPECTED_INT_VALUE_01,
                                EXPECTED_FLOAT_VALUE_01,
                                EXPECTED_DOUBLE_VALUE_01,
                                EXPECTED_STRING_VALUE_02,
                                EXPECTED_DOUBLE_VALUE_02,
                                String.valueOf(pageSize),
                                "name",
                                String.valueOf(11),
                                String.valueOf(22))
                                .collect(toList()))),
                () -> assertEquals(expectedEntry.getKey(), actualEntry.getKey()),
                () -> assertEquals(expectedEntry.getValue(), actualEntry.getValue())
        );
    }

    @Test
    void shouldExtractParametersOnClassWithOneSubclassFields() {
        val complexFieldName = "SubClass1" + "." + DOUBLE_DATA_SUBCLASS_FIELD;
        val expectedEntry = new Entry<String, String>() {
            @Override
            public String getKey() {
                return complexFieldName;
            }

            @Override
            public String getValue() {
                return String.valueOf(SOME_DOUBLE_VALUE_01);
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };

        val actualResult = getQueryParams(AlmostEmptyParentClass
                .builder()
                .subClass1(SubClass
                        .builder()
                        .doubleDataSubClass(SOME_DOUBLE_VALUE_01)
                        .build())
                .build());

        val actualEntry = actualResult.entrySet()
                .stream()
                .filter(entry -> entry.getKey()
                        .equals(complexFieldName))
                .findFirst()
                .orElse(new Entry<String, String>() {
                    @Override
                    public String getKey() {
                        return null;
                    }

                    @Override
                    public String getValue() {
                        return null;
                    }

                    @Override
                    public String setValue(String value) {
                        return null;
                    }
                });

        assertAll(
                () -> assertEquals(Stream.of(
                        EXPECTED_DOUBLE_VALUE_01).count(), actualResult.entrySet().size()),
                () -> assertTrue(actualResult.keySet().containsAll(
                        Stream.of(
                                complexFieldName)
                                .collect(toList()))),
                () -> assertTrue(actualResult.values().containsAll(
                        Stream.of(
                                EXPECTED_DOUBLE_VALUE_01)
                                .collect(toList()))),
                () -> assertEquals(expectedEntry.getKey(), actualEntry.getKey()),
                () -> assertEquals(expectedEntry.getValue(), actualEntry.getValue())
        );
    }

    @Test
    void shouldExtractParametersFromEventListRequest() {
        val complexFieldName01 = "PagedRequest" + "." + "PageSize";
        val complexFieldName02 = "PagedRequest" + "." + "CurrentPage";
        val addressValue = "addressValue";
        val pageSizeValue = 500;
        val currentPageValue = 1;
        val expectedEntry = new Entry<String, String>() {
            @Override
            public String getKey() {
                return complexFieldName01;
            }

            @Override
            public String getValue() {
                return "500";
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };

        val actualResult = getQueryParams(EventListRequest
                .builder()
                .address(addressValue)
                .pagedRequest(com.lykke.tests.selftest.queryparams.model.b.PagedRequestModel
                        .builder()
                        .pageSize(pageSizeValue)
                        .currentPage(currentPageValue)
                        .build())
                .build());

        val actualEntry = actualResult.entrySet()
                .stream()
                .filter(entry -> entry.getKey()
                        .equals(complexFieldName01))
                .findFirst()
                .orElse(new Entry<String, String>() {
                    @Override
                    public String getKey() {
                        return null;
                    }

                    @Override
                    public String getValue() {
                        return null;
                    }

                    @Override
                    public String setValue(String value) {
                        return null;
                    }
                });

        assertAll(
                () -> assertEquals(Stream.of(
                        addressValue,
                        pageSizeValue,
                        currentPageValue).count(), actualResult.entrySet().size()),
                () -> assertTrue(actualResult.keySet().containsAll(
                        Stream.of(
                                "Address",
                                complexFieldName01,
                                complexFieldName02)
                                .collect(toList()))),
                () -> assertTrue(actualResult.values().containsAll(
                        Stream.of(
                                addressValue,
                                String.valueOf(pageSizeValue),
                                String.valueOf(currentPageValue))
                                .collect(toList()))),
                () -> assertEquals(expectedEntry.getKey(), actualEntry.getKey()),
                () -> assertEquals(expectedEntry.getValue(), actualEntry.getValue())
        );
    }

    @Test
    void shouldNotExtractParametersFromConstantClass() {
        Map<String, String> actualResult = getQueryParams(ConstantParentClass
                .builder()
                .subClass1(ConstantSubClass
                        .constantClassBuilder()
                        .build())
                .build());

        assertEquals(0, actualResult.entrySet().size());
    }

    @Test
    void shouldExtractParametersFromCampaignListRequestClass22222222() {
        // TODO: skip member classes
        Map<String, String> actualResult = getQueryParams(ParentClass
                        .builder()
                        .stringData(SOME_STRING_VALUE_01)
                        .intData(SOME_INT_VALUE_01)
                        .floatData(SOME_FLOAT_VALUE_01)
                        .doubleData(SOME_DOUBLE_VALUE_01)
                        .subClass1(SubClass.builder().build())
                        .subClass2(SubClass.builder().build())
                        .objectListRequest0(ObjectListRequest.campaignBuilder().build())
                        .pagedRequest(PagedRequestModel.builder().build())
                        .build(),
                x -> !EMPTY.equalsIgnoreCase(x)
                        && !ZERO_DOUBLE_IGNORED.equalsIgnoreCase(x)
                        && !ZERO_INT_IGNORED.equalsIgnoreCase(x));

        assertEquals(4, actualResult.entrySet().size());
    }

    @Test
    void shouldExtractParametersFromCampaignListRequestClass() {
        Map<String, String> actualResult = getQueryParams(ObjectListRequest
                .campaignBuilder()
                .campaignName(SOME_STRING_VALUE_02)
                .pageSize(SOME_INT_VALUE_01)
                .currentPage(SOME_INT_VALUE_03)
                .build());

        assertAll(
                () -> assertEquals(Stream.of(
                        EXPECTED_STRING_VALUE_02,
                        EXPECTED_INT_VALUE_01,
                        EXPECTED_INT_VALUE_03).count(), actualResult.entrySet().size()),
                () -> assertTrue(actualResult.keySet().containsAll(
                        Stream.of(
                                "CampaignName",
                                "PageSize",
                                "CurrentPage")
                                .collect(toList()))),
                () -> assertTrue(actualResult.values().containsAll(
                        Stream.of(
                                EXPECTED_STRING_VALUE_02,
                                EXPECTED_INT_VALUE_01,
                                EXPECTED_INT_VALUE_03)
                                .collect(toList())))
        );
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class EmptyClass {

    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class OnlyStringClass {

        private String stringData1;
        private String stringData2;
        private String stringData3;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class SimpleClass {

        private String stringData;
        private int intData;
        private float floatData;
        private double doubleData;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class ClassWithArrays {

        private String stringData;
        private String[] stringArrayData;
        private int intData;
        private Integer[] intArrayData;
        private float floatData;
        private Float[] floatArrayData;
        private Double doubleData;
        private Double[] doubleArrayData;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class ParentClass {

        private static final String CONSTANT_VALUE_AAA = "aaa";
        private String stringData;
        private int intData;
        private float floatData;
        private double doubleData;
        private SubClass subClass1;
        private SubClass subClass2;
        private PagedRequestModel pagedRequest;
        private ObjectListRequest objectListRequest0;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class AlmostEmptyParentClass {

        private SubClass subClass1;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class SubClass {

        private static final String CONSTANT_VALUE_BBB = "bbb";
        private String stringDataSubClass;
        private int intDataSubClass;
        private float floatDataSubClass;
        private Double doubleDataSubClass;
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class ConstantParentClass {

        private static final String CONSTANT_VALUE_AAA = "aaa";
        private ConstantSubClass subClass1;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ConstantSubClass extends ConstantSuperClass {

        private static final String CONSTANT_VALUE_BBB = "bbb";

        @Builder(builderMethodName = "constantClassBuilder")
        public ConstantSubClass() {
            super();
        }
    }

    @AllArgsConstructor
    @Builder
    @Data
    public static class ConstantSuperClass {

        private static final String CONSTANT_VALUE_CCC = "ccc";
    }
}
