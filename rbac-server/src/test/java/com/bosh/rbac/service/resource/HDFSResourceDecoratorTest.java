package com.bosh.rbac.service.resource;

import com.bosh.rbac.model.Resource;
import com.bosh.rbac.model.ResourceType;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("ut")
class HDFSResourceDecoratorTest {

    @Mock
    private Resource resource;
    @Autowired
    private HDFSResourceDecorator resourceDecorator;

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> resourceDecorator.decorate(null));

        when(resource.getType()).thenReturn(ResourceType.COLUMN);
        assertEquals(Lists.newArrayList(), resourceDecorator.decorate(resource));

        when(resource.getType()).thenReturn(ResourceType.HDFS);
        when(resource.getValue()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> resourceDecorator.decorate(resource));

        when(resource.getValue()).thenReturn("");
        assertThrows(IllegalArgumentException.class, () -> resourceDecorator.decorate(resource));

        when(resource.getValue()).thenReturn(" ");
        assertThrows(IllegalArgumentException.class, () -> resourceDecorator.decorate(resource));
    }

    @Test
    void testDecorate() {
        when(resource.getType()).thenReturn(ResourceType.HDFS);
        when(resource.getValue()).thenReturn("hdfs://localhost:8020/home//work/data/partition0.parquet");

        assertEquals(Lists.newArrayList(
                new Resource(ResourceType.HDFS, "hdfs://localhost:8020"),
                new Resource(ResourceType.HDFS, "hdfs://localhost:8020/home"),
                new Resource(ResourceType.HDFS, "hdfs://localhost:8020/home/work"),
                new Resource(ResourceType.HDFS, "hdfs://localhost:8020/home/work/data")
        ), resourceDecorator.decorate(resource));
    }
}
