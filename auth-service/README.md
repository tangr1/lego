# 接口安全设计及Spring OAuth + Spring Security实现

## 设计

可以基于OAuth的概念来设计：

* 资源服务器：数据资源服务，负责如公司、个人、商品等信息的增删改查，可拆分为多个。同时资源服务器会在创建资源的时候写入权限，在访问资源的时候验证权限
* 认证服务器：Spring OAuth实现
* 客户端：使用资源服务的用户，可以是需要调用资源API的任一个后台模块，也可以是App端或浏览器端需要直接调用资源API

如图所示：

![认证鉴权架构图](https://github.com/ctofunds/louxiaoer/blob/master/spring-security/architecture.png)

一个典型的认证鉴权流程是：

1. 客户端向认证服务器发起认证请求，包含客户端ID和Secret，以及可能的用户名密码
2. 认证服务器从MySQL读取相关信息来验证用户，如果认证失败，则返回401
3. 认证服务器生成Token写入Redis
4. 认证服务器返回Token给客户端
5. 客户端用收到的Token访问资源服务器
6. 资源服务器从Redis拿到Token对应的用户信息
7. 资源服务器根据客户端访问的资源ID和具体操作进行用户鉴权
8. 资源服务器返回资源信息给客户端，如果鉴权失败，则返回403

后续客户端将一直使用同样的Token，因此1-4步无需重复

### 认证

* 后台：使用OAuth的client_credentials的认证类型，即没有用户概念，某一后台使用自己的客户端ID和secret进行认证，ID和secret属于该后台的配置
* App/浏览器：使用OAuth的password认证类型，某一类型的客户端（如安卓端）使用自己的客户端ID和secret，同时加上当前使用该端的用户名、密码进行认证。ID和secret属于改客户端的配置，对用户不可见；用户名密码需要用户主动输入，即可在用登录App或网页时完成这一认证

认证成功后获得token，后续将使用此token访问

### API鉴权

通过Spring Security对API进行权限配置，保证指定的用户角色才能有权限访问

```
http
    .authorizeRequests()
    .antMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
    .antMatchers("/**").authenticated();
```

上面代码中的配置表示访问`POST /users`接口需要角色是`ADMIN`的用户

### 资源鉴权

通过Spring Security ACL做到对每个资源精准鉴权，需要在创建资源时写入对应的权限，在访问资源时检测

#### 创建资源

```
public Company create(final @RequestBody Company request) {
	Company company = companyRepository.save(request);
	ObjectIdentity identity = new ObjectIdentityImpl(company);
	MutableAcl acl = mutableAclService.createAcl(identity);

	acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION,
			new PrincipalSid(SecurityContextHolder.getContext().getAuthentication()),
			true);

	mutableAclService.updateAcl(acl);
	return company;
}
```

这里相当于给当前创建的company对象加上了一条权限，即创建人有`ADMINISTRATION`权限，意味着可读可写可删除

#### 修改资源：

```
@PreAuthorize("hasPermission(#id, 'com.tangr1.security.domain.Company', 'administration')")
public Company update(final @PathVariable Long id, final @RequestBody Company request) {
	Company company = companyRepository.findOne(id);
	company.setName(request.getName());
	return companyRepository.save(company);
}
```

这里是当用户想修改某公司时，需要检查该用户是否对于该公司有`ADMINISTRATION`权限

### Demo运行

1. 本地启动MySQL，并创建空数据库security
1. 本地启动Redis-server
1. 在命令行指定MySQL的用户名密码，默认是root/password

  ```
  mvn -Dspring.datasource.username=root -Dspring.datasource.password=password spring-boot:run
  ```

1. 导入测试数据

  ```
  mysql -uroot -ppassword security -e 'source src/main/resources/import.sql'
  ```

1. 测试

  1. 以某应用后台角色进行认证：`curl -u vms_backend:password 'http://localhost:8080/oauth/token?grant_type=client_credentials' -X POST`
  2. 用前面结果中的access_token创建资源：`curl -H "Content-type: application/json" -H "Authorization: Bearer 6af15c5e-3a83-4275-8f8f-428d20e0be43" localhost:8080/companies -d '{"name":"公司1"}'`
  3. 以某客户端角色进行认证：`curl -u vms_app:password 'http://localhost:8080/oauth/token?username=18911111111&password=password&grant_type=password' -X POST`
  4. 以客户端的access_token访问资源：`curl -H "Authorization: Bearer 3edca2a4-a8c8-4289-bcf6-fb486325b672" localhost:8080/companies/1`，没有问题，因为代码中设置所有用户都可读
  5. 以客户端的access_token删除资源：`curl -H "Authorization: Bearer 3edca2a4-a8c8-4289-bcf6-fb486325b672" localhost:8080/companies/1 -X DELETE`，会报403错误无权限
  6. 以客户端的access_token删除资源：`curl -H "Authorization: Bearer 6af15c5e-3a83-4275-8f8f-428d20e0be43" localhost:8080/companies/1 -X DELETE`，成功，因为此用户才有`ADMINISTRATION`权限

注意：上面测试中的access_token都是随机生成，实际执行时要换成自己的
