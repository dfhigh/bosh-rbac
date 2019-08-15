package com.bosh.rbac.service;

import com.bosh.rbac.mapper.RbacMapper;
import com.bosh.rbac.model.Entity;
import com.bosh.rbac.model.EntityType;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Role;
import com.bosh.rbac.model.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mib.rest.exception.ResourceNotFoundException;
import org.mib.rest.model.list.Filter;
import org.mib.rest.model.list.ListElementRequest;
import org.mib.rest.model.list.ListPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EntityReadService {

    private final RbacMapper mapper;

    @Autowired
    public EntityReadService(final RbacMapper mapper) {
        this.mapper = mapper;
    }

    public User getUser(String userId) {
        User user = mapper.getUser(userId);
        if (user == null) throw new ResourceNotFoundException("no user found for " + userId);
        return user;
    }

    public ListPayload<User> listUsers(long offset, long limit, String search) {
        log.debug("listing users with offset {} limit {} and search query {}...", offset, limit, search);
        ListElementRequest ler = ListElementRequest.builder().offset(offset).limit(limit).build();
        if (StringUtils.isNotBlank(search)) {
            ler.setFilters(Lists.newArrayList(Filter.like("username", search)));
            ler.escaped();
        }
        return ListPayload.<User>builder().offset(offset).list(mapper.listUsers(ler))
                .total(mapper.countUsers(ler)).build();
    }

    public ListPayload<User> listRoleUsers(long roleId, long offset, long limit) {
        return ListPayload.<User>builder().offset(offset).list(mapper.listRoleUsers(roleId, offset, limit))
                .total(mapper.countRoleUsers(roleId)).build();
    }

    public Role getRole(long roleId) {
        Role role = mapper.getRole(roleId);
        if (role == null) throw new ResourceNotFoundException("no role found for " + roleId);
        return role;
    }

    public ListPayload<Role> listRoles(long offset, long limit, String search) {
        log.debug("listing roles with offset {} limit {} and search query {}...", offset, limit, search);
        ListElementRequest ler = ListElementRequest.builder().offset(offset).limit(limit).build();
        if (StringUtils.isNotBlank(search)) {
            ler.setFilters(Lists.newArrayList(Filter.like("name", search)));
            ler.escaped();
        }
        return ListPayload.<Role>builder().offset(offset).list(mapper.listRoles(ler))
                .total(mapper.countRoles(ler)).build();
    }

    public ListPayload<Role> listUserRoles(long userId, long offset, long limit) {
        return ListPayload.<Role>builder().offset(offset).list(mapper.listUserRoles(userId, offset, limit))
                .total(mapper.countUserRoles(userId)).build();
    }

    public Policy getPolicy(long policyId) {
        Policy policy = mapper.getPolicy(policyId);
        if (policy == null) throw new ResourceNotFoundException("no policy found for " + policyId);
        return policy;
    }

    public ListPayload<Policy> listPolicies(long offset, long limit, String search) {
        log.debug("listing policies with offset {} limit {} and search query {}...", offset, limit, search);
        ListElementRequest ler = ListElementRequest.builder().offset(offset).limit(limit).build();
        if (StringUtils.isNotBlank(search)) {
            ler.setFilters(Lists.newArrayList(Filter.like("name", search)));
            ler.escaped();
        }
        return ListPayload.<Policy>builder().offset(offset).list(mapper.listPolicies(ler))
                .total(mapper.countPolicies(ler)).build();
    }

    public ListPayload<Policy> listEntityPolicies(Entity entity, long offset, long limit) {
        return ListPayload.<Policy>builder().offset(offset).list(mapper.listEntityPolicies(entity, offset, limit))
                .total(mapper.countEntityPolicies(entity)).build();
    }

    public ListPayload<Entity> listPolicyEntities(long policyId, long offset, long limit) {
        ListPayload<Entity> lp = ListPayload.<Entity>builder().offset(offset).total(mapper.countPolicyEntities(policyId)).build();
        List<Entity> identities = mapper.listPolicyEntities(policyId, offset, limit);
        Set<Long> userIds = identities.stream().filter(
                e -> e.getType() == EntityType.User
        ).map(Entity::getId).collect(Collectors.toSet());
        ListElementRequest ler = ListElementRequest.builder().offset(0).limit(userIds.size())
                .ins(Maps.newHashMapWithExpectedSize(1)).build();
        ler.getIns().put("id", userIds);
        List<User> users = userIds.isEmpty() ? ImmutableList.of() : mapper.listUsers(ler);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Set<Long> roleIds = identities.stream().filter(
                e -> e.getType() == EntityType.Role
        ).map(Entity::getId).collect(Collectors.toSet());
        ler.setLimit(roleIds.size());
        ler.getIns().put("id", roleIds);
        List<Role> roles = roleIds.isEmpty() ? ImmutableList.of() : mapper.listRoles(ler);
        Map<Long, Role> roleMap = roles.stream().collect(Collectors.toMap(Role::getId, Function.identity()));
        List<Entity> entities = identities.stream().map(e ->
            e.getType() == EntityType.Role ? roleMap.get(e.getId()) : userMap.get(e.getId())
        ).collect(Collectors.toList());
        lp.setList(entities);
        return lp;
    }
}
