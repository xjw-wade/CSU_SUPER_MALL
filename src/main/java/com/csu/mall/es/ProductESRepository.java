package com.csu.mall.es;




//需要和 jpa 的dao ，放在不同的包下，因为 jpa 的dao 做了 链接 redis 的
// 如果放在同一个包下，会彼此影响，出现启动异常。

import com.csu.mall.pojo.Product;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//ElasticsearchRepository解释：
//Springboot提供了对 ElasticSearch专门的jpa的，就叫叫做 ElasticsearchRepository。
//用来做ElasticSearch JPA操作的api 跟Redis一样，在主入口程序上标上注解
@Repository
public interface ProductESRepository extends ElasticsearchRepository<Product,Integer> {
           Page<Product> findByNameLike(String name);
}

