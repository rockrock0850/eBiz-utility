package com.ebizprise.project.utility.bean;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.ebizprise.project.utility.vo.TestVO;

public class BeanUtilTest {

    @Test
    public void testJEscape () {
        TestVO vo = new TestVO();
        vo.setFakeCol1("fakeCol1");
        vo.setFakeCol2("fake'Col2");
        vo.setFakeCol3("fak''eCol3");
        vo.setFakeCol4("fakeC'''ol4");
        vo.setFakeCol5("fakeC''''ol5");
        String json = BeanUtil.toJson(vo);
        assertNotNull(BeanUtil.jEscape(json));
        System.out.println(BeanUtil.jEscape(json));
    }

    @Test
    public void testToMap () {
        TestVO vo = new TestVO();
        vo.setFakeCol1("fakeCol1");
        vo.setFakeCol2("fake'Col2測試修改評論內容");
        vo.setFakeCol3("fak''eCol3");
        vo.setFakeCol4("fakeC'''ol4");
        vo.setFakeCol5("fakeC''''ol5");
        Map<String, Object> map = BeanUtil.toMap(vo);
        assertNotNull(map);
        System.out.println(map);
    }

    @Test
    public void testCopyList () {
        fail("Not yet implemented");
    }

    @Test
    public void testToJsonObject () {
        fail("Not yet implemented");
    }

    @Test
    public void testToJsonObjectGsonBuilder () {
        fail("Not yet implemented");
    }

    @Test
    public void testFromJsonString () {
        fail("Not yet implemented");
    }

    @Test
    public void testFromJsonStringClassOfT () {
        fail("Not yet implemented");
    }

    @Test
    public void testFromJsonStringGsonBuilderClassOfT () {
        fail("Not yet implemented");
    }

    @Test
    public void testFromJsonToListStringClassOfQ () {
        fail("Not yet implemented");
    }

    @Test
    public void testFromJsonToListStringGsonBuilderClassOfQ () {
        fail("Not yet implemented");
    }

    @Test
    public void testCopyPropertiesObjectObjectBoolean () {
        fail("Not yet implemented");
    }

    @Test
    public void testCopyPropertiesObjectObject () {
        fail("Not yet implemented");
    }

    @Test
    public void testCopyPropertiesObjectObjectClassOfQ () {
        fail("Not yet implemented");
    }

    @Test
    public void testCopyPropertiesObjectObjectStringArray () {
        fail("Not yet implemented");
    }

}
