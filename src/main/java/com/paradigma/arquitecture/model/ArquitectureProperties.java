package com.paradigma.arquitecture.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The Class ArquitectureProperties.
 *
 * @author Javier Ledo Vázquez
 * @version 1.0
 */
@ConfigurationProperties( prefix = "arquitecture", ignoreUnknownFields = false)
public class ArquitectureProperties {

	private final Security security = new Security();
	private final Data data = new Data();

	public Security getSecurity() {
		return security;
	}

	public Data getData() {
		return data;
	}

	public static class Data {

		private final Mongodb mongodb = new Mongodb();

		public Mongodb getMongodb() {
			return mongodb;
		}

		public static class Mongodb {
			private String host;
			private int port;
			private String database;
			private String username;
			private String password;

			public String getHost() {
				return host;
			}

			public void setHost(String host) {
				this.host = host;
			}

			public int getPort() {
				return port;
			}

			public void setPort(int port) {
				this.port = port;
			}

			public String getDatabase() {
				return database;
			}

			public void setDatabase(String database) {
				this.database = database;
			}

			public String getUsername() {
				return username;
			}

			public void setUsername(String username) {
				this.username = username;
			}

			public String getPassword() {
				return password;
			}

			public void setPassword(String password) {
				this.password = password;
			}

		}
	}

	public static class Security {
		private final Authentication authentication = new Authentication();

		public Authentication getAuthentication() {
			return authentication;
		}

		public static class Authentication {

			private final Oauth oauth = new Oauth();
			private final AccessToken accessToken = new AccessToken();

			public Oauth getOauth() {
				return oauth;
			}

			public AccessToken getAccessToken() {
				return accessToken;
			}

			public static class Oauth {

				private String clientid;

				private String secret;

				private int tokenValidityInSeconds = 1800;

				public String getClientid() {
					return clientid;
				}

				public void setClientid(String clientid) {
					this.clientid = clientid;
				}

				public String getSecret() {
					return secret;
				}

				public void setSecret(String secret) {
					this.secret = secret;
				}

				public int getTokenValidityInSeconds() {
					return tokenValidityInSeconds;
				}

				public void setTokenValidityInSeconds(int tokenValidityInSeconds) {
					this.tokenValidityInSeconds = tokenValidityInSeconds;
				}
			}

			public static class AccessToken {

				private String secret;

				private boolean storeUserInToken;

				private int tokenValidityInSeconds = 1800;

				public String getSecret() {
					return secret;
				}

				public void setSecret(String secret) {
					this.secret = secret;
				}

				public int getTokenValidityInSeconds() {
					return tokenValidityInSeconds;
				}

				public void setTokenValidityInSeconds(int tokenValidityInSeconds) {
					this.tokenValidityInSeconds = tokenValidityInSeconds;
				}

				public boolean isStoreUserInToken() {
					return storeUserInToken;
				}

				public void setStoreUserInToken(boolean storeUserInToken) {
					this.storeUserInToken = storeUserInToken;
				}

			}
		}

	}
}
