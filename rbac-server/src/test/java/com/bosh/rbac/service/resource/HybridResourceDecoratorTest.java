package com.bosh.rbac.service.resource;

import com.bosh.rbac.model.Resource;
import com.bosh.rbac.model.ResourceType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("ut")
class HybridResourceDecoratorTest {

    @Mock
    private Resource resource;
    @Mock
    private List<Resource> decoratedResources;
    @MockBean
    private HDFSResourceDecorator hdfs;
    @MockBean
    private ColumnResourceDecorator column;

    @Autowired
    @Qualifier("resourceDecorator")
    private ResourceDecorator resourceDecorator;

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> resourceDecorator.decorate(null));

        when(resource.getType()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> resourceDecorator.decorate(resource));
    }

    @Test
    void testHDFS() {
        when(resource.getType()).thenReturn(ResourceType.HDFS);
        when(hdfs.decorate(resource)).thenReturn(decoratedResources);

        assertEquals(decoratedResources, resourceDecorator.decorate(resource));
        verify(hdfs).decorate(resource);
        verify(column, never()).decorate(resource);
    }

    @Test
    void testColumn() {
        when(resource.getType()).thenReturn(ResourceType.COLUMN);
        when(column.decorate(resource)).thenReturn(decoratedResources);

        assertEquals(decoratedResources, resourceDecorator.decorate(resource));
        verify(hdfs, never()).decorate(resource);
        verify(column).decorate(resource);
    }
}
