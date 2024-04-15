package com.example.demo.repositories;

import com.example.demo.model.entity.ShoppingCartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartDetailRepository extends JpaRepository<ShoppingCartDetail,Long> {
}

