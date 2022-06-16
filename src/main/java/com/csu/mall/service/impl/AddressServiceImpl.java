package com.csu.mall.service.impl;

import com.csu.mall.persistence.AddressRepository;
import com.csu.mall.pojo.Address;
import com.csu.mall.pojo.User;
import com.csu.mall.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //表明该类是一个服务类
@CacheConfig(cacheNames = "address")
public class AddressServiceImpl implements AddressService {
    @Autowired
    AddressRepository addressRepository;

    @Override
    @Cacheable(key="'addressList-uid-'+ #p0.id")
    public List<Address> listByUser(User user) {
        return addressRepository.findByUser(user);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void update(Address address) {
        addressRepository.save(address);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void add(Address address) {
        addressRepository.save(address);
    }

    @Override
    @Cacheable(key = "'address-one'+#p0")
    public Address getById(int id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteById(int id) {
        addressRepository.deleteById(id);
    }
}
