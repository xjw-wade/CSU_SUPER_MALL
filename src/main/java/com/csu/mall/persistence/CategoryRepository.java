package com.csu.mall.persistence;

import com.csu.mall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//继承Jpa 集成CRUD的底层JpaRepository，第一个参数要操作的对象，第二个参数为主键类型
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
