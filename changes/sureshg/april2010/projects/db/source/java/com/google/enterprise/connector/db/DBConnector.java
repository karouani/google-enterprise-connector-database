// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.db;

import java.util.Map;

import com.google.enterprise.connector.spi.Connector;
import com.google.enterprise.connector.spi.RepositoryException;
import com.google.enterprise.connector.spi.Session;

/**
 * Implementation of {@link Connector} for the database connector. This provides
 * session to the connector manager.
 */

public class DBConnector implements Connector {
	private final DBContext dbContext;
	private final String sqlQuery;
	private final String googleConnectorWorkDir;
	private final String primaryKeysString;
	private final String xslt;
	private Map<String, String> dbTypeDriver = null;

	// baseURL is a partial URL when combined with the value of document id can
	// return the URL of the document to be indexed. For example, when base URL
	// is "http://<host-name>/<web-app>?doc_id=" is concatenated with document
	// id stored in column of the database table you can get URL of the document
	// to be crawled and indexed.
	// http://<host-name>/<web-app>?doc_id=43567
	private final String baseURL;

	public DBConnector(String connectionUrl, String hostname,
			String driverClassName, String login, String password,
			String dbName, String sqlQuery, String googleConnectorWorkDir,
			String primaryKeysString, String xslt, String baseURL) {
		this.dbContext = new DBContext(connectionUrl, hostname,
				driverClassName, login, password, dbName);
		this.sqlQuery = sqlQuery;
		this.googleConnectorWorkDir = googleConnectorWorkDir;
		this.primaryKeysString = primaryKeysString;
		this.xslt = xslt;
		this.baseURL = baseURL;
	}

	public Map<String, String> getDbTypeDriver() {
		return dbTypeDriver;
	}

	/* @Override */
	public Session login() throws RepositoryException {
		DBClient dbClient;
		try {
			dbClient = new DBClient(dbContext, sqlQuery,
					googleConnectorWorkDir,
					primaryKeysString.split(Util.PRIMARY_KEYS_SEPARATOR),
					baseURL);
			return new DBSession(dbClient, xslt);
		} catch (DBException e) {
			throw new RepositoryException("Could not create DB client.",
					e.getCause());
		}
	}
}