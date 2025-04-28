/*
 * AbstractHibernateDaoTest.java                                 22 avr. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.generic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * This class test classes {@link AbstractHibernateDao} and {@link GenericDao}, last class cited
 * is undirectly tested by its implementation
 */
class AbstractHibernateDaoTest {

    /**
     * Class to test DAO, given that DAO do not take a specific class,
     * we create one there with id only to make some calls
     */
    private static class TestEntity {
        private Long id;

        public TestEntity() {}

        public TestEntity(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    private static class ConcreteDao extends AbstractHibernateDao<TestEntity, Long> {}

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<TestEntity> query;

    private ConcreteDao dao;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        dao = new ConcreteDao();
        // Injection de l'EntityManager mocké
        when(entityManager.createQuery(anyString())).thenReturn(query);
        try {
            Field field = AbstractHibernateDao.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(dao, entityManager);
        } catch (Exception e) {
            fail("Impossible d'injecter l'EntityManager: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testFindById() {
        // Given
        TestEntity expectedEntity = new TestEntity(1L);
        when(entityManager.find(TestEntity.class, 1L)).thenReturn(expectedEntity);

        // When
        TestEntity result = dao.findById(1L);

        // Then
        assertEquals(expectedEntity, result);
        verify(entityManager).find(TestEntity.class, 1L);
    }

    @Test
    void testFindAll() {
        // Given
        List<TestEntity> expectedEntities = Arrays.asList(
                new TestEntity(1L),
                new TestEntity(2L)
        );
        when(query.getResultList()).thenReturn(expectedEntities);

        // When
        List<TestEntity> result = dao.findAll();

        // Then
        assertEquals(expectedEntities, result);
        verify(entityManager).createQuery(anyString());
        verify(query).getResultList();
    }

    @Test
    void testSave() {
        // Given
        TestEntity entity = new TestEntity(1L);

        // When
        dao.save(entity);

        // Then
        verify(entityManager).persist(entity);
    }

    @Test
    void testUpdate() {
        // Given
        TestEntity entity = new TestEntity(1L);
        when(entityManager.merge(entity)).thenReturn(entity);

        // When
        dao.update(entity);

        // Then
        verify(entityManager).merge(entity);
    }

    @Test
    void testDelete() {
        // Given
        TestEntity entity = new TestEntity(1L);

        // When
        dao.delete(entity);

        // Then
        verify(entityManager).remove(entity);
    }

    @Test
    void testDeleteById() {
        // Given
        TestEntity entity = new TestEntity(1L);
        when(entityManager.find(TestEntity.class, 1L)).thenReturn(entity);

        // When
        dao.deleteById(1L);

        // Then
        verify(entityManager).find(TestEntity.class, 1L);
        verify(entityManager).remove(entity);
    }

    @Test
    void testDeleteByIdWhenEntityNotFound() {
        // Given
        when(entityManager.find(TestEntity.class, 1L)).thenReturn(null);

        // When
        dao.deleteById(1L);

        // Then
        verify(entityManager).find(TestEntity.class, 1L);
        verify(entityManager, never()).remove(any());
    }
}
