/*
 * DaoHibernateAbstraitTest.java                                 22 avr. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.generic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
 * Cette classe teste la classe {@link DaoHibernateAbstrait} et {@link DaoGenerique}, la dernière classe citée
 * est testée indirectement via son implémentation
 */
class DaoHibernateAbstraitTest {

    /**
     * Classe permettant de tester le DAO, étant donné que le DAO ne prend pas de classe spécifique, on en
     * crée une ici avec un id seulement pour effectuer des appels
     */
    private static class EntiteTest {
        private Long id;

        public EntiteTest() {}

        public EntiteTest(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    private static class DaoConcret extends DaoHibernateAbstrait<EntiteTest, Long> {}

    @Mock
    private EntityManager gestionnaireEntite;

    @Mock
    private TypedQuery<EntiteTest> requete;

    private DaoConcret dao;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        dao = new DaoConcret();
        // Injection de l'EntityManager mocké
        when(gestionnaireEntite.createQuery(anyString())).thenReturn(requete);
        try {
            Field field = DaoHibernateAbstrait.class.getDeclaredField("gestionnaireEntite");
            field.setAccessible(true);
            field.set(dao, gestionnaireEntite);
        } catch (Exception e) {
            fail("Impossible d'injecter le gestionnaireEntite: " + e.getMessage());
        }
    }

    @Test
    void testTrouverParId() {
        // Given
        EntiteTest entiteAttendue = new EntiteTest(1L);
        when(gestionnaireEntite.find(EntiteTest.class, 1L)).thenReturn(entiteAttendue);

        // When
        EntiteTest resultat = dao.trouverParId(1L);

        // Then
        assertEquals(entiteAttendue, resultat);
        verify(gestionnaireEntite).find(EntiteTest.class, 1L);
    }

    @Test
    void testTrouverTout() {
        // Given
        List<EntiteTest> entitesAttendues = Arrays.asList(
                new EntiteTest(1L),
                new EntiteTest(2L)
        );
        when(requete.getResultList()).thenReturn(entitesAttendues);

        // When
        List<EntiteTest> resultat = dao.trouverTout();

        // Then
        assertEquals(entitesAttendues, resultat);
        verify(gestionnaireEntite).createQuery(anyString());
        verify(requete).getResultList();
    }

    @Test
    void testSauvegarder() {
        // Given
        EntiteTest entite = new EntiteTest(1L);

        // When
        dao.sauvegarder(entite);

        // Then
        verify(gestionnaireEntite).persist(entite);
    }

    @Test
    void testMettreAJour() {
        // Given
        EntiteTest entite = new EntiteTest(1L);
        when(gestionnaireEntite.merge(entite)).thenReturn(entite);

        // When
        dao.mettreAJour(entite);

        // Then
        verify(gestionnaireEntite).merge(entite);
    }

    @Test
    void testSupprimer() {
        // Given
        EntiteTest entite = new EntiteTest(1L);

        // When
        dao.supprimer(entite);

        // Then
        verify(gestionnaireEntite).remove(entite);
    }

    @Test
    void testSupprimerParId() {
        // Given
        EntiteTest entite = new EntiteTest(1L);
        when(gestionnaireEntite.find(EntiteTest.class, 1L)).thenReturn(entite);

        // When
        dao.supprimerParId(1L);

        // Then
        verify(gestionnaireEntite).find(EntiteTest.class, 1L);
        verify(gestionnaireEntite).remove(entite);
    }

    @Test
    void testSupprimerParIdQuandEntiteNonTrouvee() {
        // Given
        when(gestionnaireEntite.find(EntiteTest.class, 1L)).thenReturn(null);

        // When
        dao.supprimerParId(1L);

        // Then
        verify(gestionnaireEntite).find(EntiteTest.class, 1L);
        verify(gestionnaireEntite, never()).remove(any());
    }
}
