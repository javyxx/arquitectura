package com.paradigma.arquitecture.provider.userdetails.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.CacheManager;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.paradigma.arquitecture.model.ArquitectureProperties.Data.Mongodb;
import com.paradigma.arquitecture.provider.userdetails.domain.Permission;
import com.paradigma.arquitecture.provider.userdetails.domain.Role;
import com.paradigma.arquitecture.provider.userdetails.domain.UserSecurityDetailsImpl;

public class MongoUserDetailsService extends AbstractUserDetailsService {
	public static final String BEAN_NAME = "mongoUserDeailsService";

	private final MongoClient mongoClient;
	private final Mongodb mongodbConfig;

	public MongoUserDetailsService(CacheManager cacheManager, Mongodb mongodbConfig) {
		super(cacheManager);
		this.mongodbConfig = mongodbConfig;
		this.mongoClient = getMongoClient(mongodbConfig);

	}

	private MongoClient getMongoClient(Mongodb mongodbConfig) {
		try {
			List<MongoCredential> credentials = new ArrayList<>();
			MongoCredential mongoCredential = MongoCredential.createMongoCRCredential(mongodbConfig.getUsername(),
					mongodbConfig.getDatabase(), mongodbConfig.getPassword().toCharArray());
			credentials.add(mongoCredential);
			MongoClient mongoClient = new MongoClient(new ServerAddress(mongodbConfig.getHost()), credentials);
			return mongoClient;

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;

	}

	protected UserSecurityDetailsImpl findUser(String username) {
		BasicDBObject resultado = (BasicDBObject) mongoClient.getDB(mongodbConfig.getDatabase()).getCollection("user")
				.findOne(new BasicDBObject().append("username", username));

		if (resultado != null) {

			List<Role> roles = new ArrayList<>();
			BasicDBList rolesDB = (BasicDBList) resultado.get("roles");
			for (Object roleObject : rolesDB) {
				BasicDBObject roleDB = (BasicDBObject) roleObject;
				String roleName = roleDB.getString("name");
				List<Permission> permissions = new ArrayList<>();

				if (roleDB.containsField("permissions")) {
					BasicDBList permissionsDB = (BasicDBList) roleDB.get("permissions");
					for (Object permissionObject : permissionsDB) {
						BasicDBObject permissionDB = (BasicDBObject) permissionObject;
						String resource = permissionDB.getString("resource");
						String method = permissionDB.getString("method");
						BasicDBList allowIds = (BasicDBList) permissionDB.get("allowIds");
						BasicDBList denyIds = (BasicDBList) permissionDB.get("denyIds");
						boolean allowAll = permissionDB.getBoolean("allowAll", false);
						boolean ownEntities = permissionDB.getBoolean("ownEntities", false);

						Permission permission = new Permission();
						permission.setResource(resource);
						permission.setMethod(method);
						permission.setAllowIds(allowIds);
						permission.setDenyIds(denyIds);
						permission.setAllowAll(allowAll);
						permission.setOwnEntities(ownEntities);

						permissions.add(permission);

					}
				}

				Role role = new Role(roleName, permissions);
				roles.add(role);
			}

			String password = resultado.getString("password");
			boolean accountNonExpired = true;
			boolean accountNonLocked = true;
			boolean credentialsNonExpired = true;
			boolean enabled = true;
			UserSecurityDetailsImpl user = new UserSecurityDetailsImpl(resultado, roles, password, username,
					accountNonExpired, accountNonLocked, credentialsNonExpired, enabled);
			return user;

		}
		return null;

	}

}