/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.rs.security.oauth2.utils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthDataProvider;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;


/**
 * Default Model Encryption helpers
 */
public final class ModelEncryptionSupport {
    private static final String SEP = "|";
    private ModelEncryptionSupport() {
    }
    
    public static String encryptClient(Client client, Key secretKey) throws EncryptionException {
        return encryptClient(client, secretKey, null);
    }
     
    public static String encryptClient(Client client, Key secretKey,
                                       SecretKeyProperties props) throws EncryptionException {
        String tokenSequence = tokenizeClient(client);
        return EncryptionUtils.encryptSequence(tokenSequence, secretKey, props);
    }
    
    public static String encryptAccessToken(ServerAccessToken token, Key secretKey) throws EncryptionException {
        return encryptAccessToken(token, secretKey, null);
    }
    
    public static String encryptAccessToken(ServerAccessToken token, Key secretKey,
                                            SecretKeyProperties props) throws EncryptionException {
        String tokenSequence = tokenizeServerToken(token);
        return EncryptionUtils.encryptSequence(tokenSequence, secretKey, props);
    }
    
    public static String encryptRefreshToken(RefreshToken token, Key secretKey) throws EncryptionException {
        return encryptRefreshToken(token, secretKey, null);
    }
    
    public static String encryptRefreshToken(RefreshToken token, Key secretKey,
                                             SecretKeyProperties props) throws EncryptionException {
        String tokenSequence = tokenizeRefreshToken(token);
        
        return EncryptionUtils.encryptSequence(tokenSequence, secretKey, props);
    }
    
    public static String encryptCodeGrant(ServerAuthorizationCodeGrant grant, Key secretKey) 
        throws EncryptionException {
        return encryptCodeGrant(grant, secretKey, null);
    }
    
    public static String encryptCodeGrant(ServerAuthorizationCodeGrant grant, Key secretKey,
                                          SecretKeyProperties props) throws EncryptionException {
        String tokenSequence = tokenizeCodeGrant(grant);
        
        return EncryptionUtils.encryptSequence(tokenSequence, secretKey, props);
    }
    
    public static Client decryptClient(String encodedSequence, String encodedSecretKey) 
        throws EncryptionException {
        return decryptClient(encodedSequence, encodedSecretKey, new SecretKeyProperties("AES"));
    }
    
    public static Client decryptClient(String encodedSequence, String encodedSecretKey,
                                       SecretKeyProperties props) throws EncryptionException {
        SecretKey key = EncryptionUtils.decodeSecretKey(encodedSecretKey, props.getKeyAlgo());
        return decryptClient(encodedSequence, key, props);
    }
    
    public static Client decryptClient(String encodedSequence, Key secretKey) throws EncryptionException {
        return decryptClient(encodedSequence, secretKey, null);
    }
    
    public static Client decryptClient(String encodedData, Key secretKey, 
                                       SecretKeyProperties props) throws EncryptionException {
        String decryptedSequence = EncryptionUtils.decryptSequence(encodedData, secretKey, props);
        return recreateClient(decryptedSequence);
    }
    
    public static ServerAccessToken decryptAccessToken(OAuthDataProvider provider,
                                                 String encodedToken, 
                                                 String encodedSecretKey) throws EncryptionException {
        return decryptAccessToken(provider, encodedToken, encodedSecretKey, new SecretKeyProperties("AES"));
    }
    
    public static ServerAccessToken decryptAccessToken(OAuthDataProvider provider,
                                                 String encodedToken, 
                                                 String encodedSecretKey,
                                                 SecretKeyProperties props) throws EncryptionException {
        SecretKey key = EncryptionUtils.decodeSecretKey(encodedSecretKey, props.getKeyAlgo());
        return decryptAccessToken(provider, encodedToken, key, props);
    }
    
    public static ServerAccessToken decryptAccessToken(OAuthDataProvider provider,
                                                 String encodedToken, 
                                                 Key secretKey) throws EncryptionException {
        return decryptAccessToken(provider, encodedToken, secretKey, null);
    }
    
    public static ServerAccessToken decryptAccessToken(OAuthDataProvider provider,
                                                 String encodedData, 
                                                 Key secretKey, 
                                                 SecretKeyProperties props) throws EncryptionException {
        String decryptedSequence = EncryptionUtils.decryptSequence(encodedData, secretKey, props);
        return recreateAccessToken(provider, encodedData, decryptedSequence);
    }
    
    public static RefreshToken decryptRefreshToken(OAuthDataProvider provider,
                                                   String encodedToken, 
                                                   String encodedSecretKey) throws EncryptionException {
        return decryptRefreshToken(provider, encodedToken, encodedSecretKey, new SecretKeyProperties("AES"));
    }
    
    public static RefreshToken decryptRefreshToken(OAuthDataProvider provider,
                                                  String encodedToken, 
                                                  String encodedSecretKey,
                                                  SecretKeyProperties props) throws EncryptionException {
        SecretKey key = EncryptionUtils.decodeSecretKey(encodedSecretKey, props.getKeyAlgo());
        return decryptRefreshToken(provider, encodedToken, key, props);
    }
    
    public static RefreshToken decryptRefreshToken(OAuthDataProvider provider,
                                                   String encodedToken, 
                                                   Key key) throws EncryptionException {
        return decryptRefreshToken(provider, encodedToken, key, null);
    }
    
    public static RefreshToken decryptRefreshToken(OAuthDataProvider provider,
                                                   String encodedData, 
                                                   Key key, 
                                                   SecretKeyProperties props) throws EncryptionException {
        String decryptedSequence = EncryptionUtils.decryptSequence(encodedData, key, props);
        return recreateRefreshToken(provider, encodedData, decryptedSequence);
    }
    
    public static ServerAuthorizationCodeGrant decryptCodeGrant(OAuthDataProvider provider,
                                                   String encodedToken, 
                                                   String encodedSecretKey) throws EncryptionException {
        return decryptCodeGrant(provider, encodedToken, encodedSecretKey, new SecretKeyProperties("AES"));
    }
    
    public static ServerAuthorizationCodeGrant decryptCodeGrant(OAuthDataProvider provider,
                                                  String encodedToken, 
                                                  String encodedSecretKey,
                                                  SecretKeyProperties props) throws EncryptionException {
        SecretKey key = EncryptionUtils.decodeSecretKey(encodedSecretKey, props.getKeyAlgo());
        return decryptCodeGrant(provider, encodedToken, key, props);
    }
    
    public static ServerAuthorizationCodeGrant decryptCodeGrant(OAuthDataProvider provider,
                                                   String encodedToken, 
                                                   Key key) throws EncryptionException {
        return decryptCodeGrant(provider, encodedToken, key, null);
    }
    
    public static ServerAuthorizationCodeGrant decryptCodeGrant(OAuthDataProvider provider,
                                                   String encodedData, 
                                                   Key key, 
                                                   SecretKeyProperties props) throws EncryptionException {
        String decryptedSequence = EncryptionUtils.decryptSequence(encodedData, key, props);
        return recreateCodeGrant(provider, decryptedSequence);
    }
    
    public static ServerAccessToken recreateAccessToken(OAuthDataProvider provider,
                                                  String newTokenKey,
                                                  String decryptedSequence) throws EncryptionException {
        return recreateAccessToken(provider, newTokenKey, getParts(decryptedSequence));
    }
    
    public static RefreshToken recreateRefreshToken(OAuthDataProvider provider,
                                                    String newTokenKey,
                                                    String decryptedSequence) throws EncryptionException {
        String[] parts = getParts(decryptedSequence);
        ServerAccessToken token = recreateAccessToken(provider, newTokenKey, parts);
        return new RefreshToken(token, 
                                newTokenKey, 
                                parseSimpleList(parts[parts.length - 1]));
    }
    
    public static ServerAuthorizationCodeGrant recreateCodeGrant(OAuthDataProvider provider,
        String decryptedSequence) throws EncryptionException {
        return recreateCodeGrantInternal(provider, decryptedSequence);
    }
    
    public static Client recreateClient(String sequence) throws EncryptionException {
        return recreateClientInternal(sequence);
    }
    
    private static ServerAccessToken recreateAccessToken(OAuthDataProvider provider,
                                                  String newTokenKey,
                                                  String[] parts) {
        
        
        @SuppressWarnings("serial")
        final ServerAccessToken newToken = new ServerAccessToken(provider.getClient(parts[4]),
                                                                 parts[1],
                                                                 newTokenKey == null ? parts[0] : newTokenKey,
                                                                 Long.valueOf(parts[2]),
                                                                 Long.valueOf(parts[3])) {
        };  
        
        newToken.setRefreshToken(getStringPart(parts[5]));
        newToken.setGrantType(getStringPart(parts[6]));
        newToken.setAudience(getStringPart(parts[7]));
        newToken.setParameters(parseSimpleMap(parts[8]));
        
        // Permissions
        if (!parts[9].trim().isEmpty()) {
            List<OAuthPermission> perms = new LinkedList<OAuthPermission>(); 
            String[] allPermParts = parts[9].split("\\.");
            for (int i = 0; i + 4 < allPermParts.length; i = i + 5) {
                OAuthPermission perm = new OAuthPermission(allPermParts[i], allPermParts[i + 1]);
                perm.setDefault(Boolean.valueOf(allPermParts[i + 2]));
                perm.setHttpVerbs(parseSimpleList(allPermParts[i + 3]));
                perm.setUris(parseSimpleList(allPermParts[i + 4]));
                perms.add(perm);
            }
            newToken.setScopes(perms);
        }
        //UserSubject:
        newToken.setSubject(recreateUserSubject(parts[10]));
                
        return newToken;
    }
    
    private static String tokenizeRefreshToken(RefreshToken token) {
        String seq = tokenizeServerToken(token);
        return seq + SEP + token.getAccessTokens().toString();
    }
    
    private static String tokenizeServerToken(ServerAccessToken token) {
        StringBuilder state = new StringBuilder();
        // 0: key
        state.append(tokenizeString(token.getTokenKey()));
        // 1: type
        state.append(SEP);
        state.append(tokenizeString(token.getTokenType()));
        // 2: expiresIn 
        state.append(SEP);
        state.append(token.getExpiresIn());
        // 3: issuedAt
        state.append(SEP);
        state.append(token.getIssuedAt());
        // 4: client id
        state.append(SEP);
        state.append(tokenizeString(token.getClient().getClientId()));
        // 5: refresh token
        state.append(SEP);
        state.append(tokenizeString(token.getRefreshToken()));
        // 6: grant type
        state.append(SEP);
        state.append(tokenizeString(token.getGrantType()));
        // 7: audience
        state.append(SEP);
        state.append(tokenizeString(token.getAudience()));
        // 8: other parameters
        state.append(SEP);
        // {key=value, key=value}
        state.append(token.getParameters().toString());
        // 9: permissions
        state.append(SEP);
        if (token.getScopes().isEmpty()) {
            state.append(" ");
        } else {
            for (OAuthPermission p : token.getScopes()) {
                // 9.1
                state.append(tokenizeString(p.getPermission()));
                state.append(".");
                // 9.2
                state.append(tokenizeString(p.getDescription()));
                state.append(".");
                // 9.3
                state.append(p.isDefault());
                state.append(".");
                // 9.4
                state.append(p.getHttpVerbs().toString());
                state.append(".");
                // 9.5
                state.append(p.getUris().toString());
            }
        }
        state.append(SEP);
        // 10: user subject
        tokenizeUserSubject(state, token.getSubject());
        
        return state.toString();
    }
    

    private static Client recreateClientInternal(String sequence) {
        String[] parts = getParts(sequence);
        Client c = new Client(parts[0], 
                              parts[1], 
                              Boolean.valueOf(parts[2]), 
                              getStringPart(parts[3]), getStringPart(parts[4]));
        c.setApplicationDescription(getStringPart(parts[5]));
        c.setApplicationLogoUri(getStringPart(parts[6]));
        c.setAllowedGrantTypes(parseSimpleList(parts[7]));
        c.setRegisteredScopes(parseSimpleList(parts[8]));
        c.setRedirectUris(parseSimpleList(parts[9]));
        c.setRegisteredAudiences(parseSimpleList(parts[10]));
        c.setProperties(parseSimpleMap(parts[11]));
        c.setSubject(recreateUserSubject(parts[12]));
        return c; 
    }
    private static String tokenizeClient(Client client) {
        StringBuilder state = new StringBuilder();
        // 0: id
        state.append(tokenizeString(client.getClientId()));
        state.append(SEP);
        // 1: secret
        state.append(tokenizeString(client.getClientSecret()));
        state.append(SEP);
        // 2: confidentiality
        state.append(client.isConfidential());
        state.append(SEP);
        // 3: app name
        state.append(tokenizeString(client.getApplicationName()));
        state.append(SEP);
        // 4: app web URI
        state.append(tokenizeString(client.getApplicationWebUri()));
        state.append(SEP);
        // 5: app description
        state.append(tokenizeString(client.getApplicationDescription()));
        state.append(SEP);
        // 6: app logo URI
        state.append(tokenizeString(client.getApplicationLogoUri()));
        state.append(SEP);
        // 7: grants
        state.append(client.getAllowedGrantTypes().toString());
        state.append(SEP);
        // 8: redirect URIs
        state.append(client.getRedirectUris().toString());
        state.append(SEP);
        // 9: registered scopes
        state.append(client.getRegisteredScopes().toString());
        state.append(SEP);
        // 10: registered audiences
        state.append(client.getRegisteredAudiences().toString());
        state.append(SEP);
        // 11: properties
        state.append(client.getProperties().toString());
        state.append(SEP);
        // 12: subject
        tokenizeUserSubject(state, client.getSubject());
        
        return state.toString();
    }
    private static ServerAuthorizationCodeGrant recreateCodeGrantInternal(OAuthDataProvider provider,
                                                                          String sequence) {
        String[] parts = getParts(sequence);
        ServerAuthorizationCodeGrant grant = new ServerAuthorizationCodeGrant(provider.getClient(parts[0]),
                                                                              parts[1],
                                                                              Long.valueOf(parts[2]),
                                                                              Long.valueOf(parts[3]));
        grant.setRedirectUri(getStringPart(parts[4]));
        grant.setAudience(getStringPart(parts[5]));
        grant.setClientCodeVerifier(getStringPart(parts[6]));
        grant.setApprovedScopes(parseSimpleList(parts[7]));
        grant.setSubject(recreateUserSubject(parts[8]));
        return grant; 
    }
    private static String tokenizeCodeGrant(ServerAuthorizationCodeGrant grant) {
        StringBuilder state = new StringBuilder();
        // 0: client id
        state.append(grant.getClient().getClientId());
        state.append(SEP);
        // 1: code
        state.append(tokenizeString(grant.getCode()));
        state.append(SEP);
        // 2: expiresIn
        state.append(grant.getExpiresIn());
        state.append(SEP);
        // 3: issuedAt
        state.append(grant.getIssuedAt());
        state.append(SEP);
        // 4: redirect URI
        state.append(tokenizeString(grant.getRedirectUri()));
        state.append(SEP);
        // 5: audience
        state.append(tokenizeString(grant.getAudience()));
        state.append(SEP);
        // 6: code verifier
        state.append(tokenizeString(grant.getClientCodeVerifier()));
        state.append(SEP);
        // 7: approved scopes
        state.append(grant.getApprovedScopes().toString());
        state.append(SEP);
        // 8: subject
        tokenizeUserSubject(state, grant.getSubject());
        
        return state.toString();
    }
    
    private static String getStringPart(String str) {
        return " ".equals(str) ? null : str;
    }
    
    private static String prepareSimpleString(String str) {
        return str.trim().isEmpty() ? "" : str.substring(1, str.length() - 1);
    }
    
    private static List<String> parseSimpleList(String listStr) {
        String pureStringList = prepareSimpleString(listStr);
        if (pureStringList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(pureStringList.split(","));
        }
    }
    
    private static Map<String, String> parseSimpleMap(String mapStr) {
        Map<String, String> props = new HashMap<String, String>();
        List<String> entries = parseSimpleList(mapStr);
        for (String entry : entries) {
            String[] pair = entry.split("=");
            props.put(pair[0], pair[1]);
        }
        return props;
    }
    
    private static String[] getParts(String sequence) {
        return sequence.split("\\" + SEP);
    }
    
    private static UserSubject recreateUserSubject(String sequence) {
        UserSubject subject = null;
        if (!sequence.trim().isEmpty()) {
            String[] subjectParts = sequence.split("\\.");
            subject = new UserSubject(getStringPart(subjectParts[0]), getStringPart(subjectParts[1]));
            subject.setRoles(parseSimpleList(subjectParts[2]));
            subject.setProperties(parseSimpleMap(subjectParts[3]));
        }
        return subject;
        
        
    }
    
    private static void tokenizeUserSubject(StringBuilder state, UserSubject subject) {
        if (subject != null) {
            // 1
            state.append(tokenizeString(subject.getLogin()));
            state.append(".");
            // 2
            state.append(tokenizeString(subject.getId()));
            state.append(".");
            // 3
            state.append(subject.getRoles().toString());
            state.append(".");
            // 4
            state.append(subject.getProperties().toString());
        } else {
            state.append(" ");
        }
    }
    
    private static String tokenizeString(String str) {
        return str != null ? str : " ";
    }
}
