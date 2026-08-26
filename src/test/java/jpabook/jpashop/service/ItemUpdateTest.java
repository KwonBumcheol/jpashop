package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        // Given 준비
        Book book = em.find(Book.class, 1L);

        // When 실행
        book.setName("aesfadsfe");

        // 변경감지 == dirty checking
        // Then 검증

    }
}
