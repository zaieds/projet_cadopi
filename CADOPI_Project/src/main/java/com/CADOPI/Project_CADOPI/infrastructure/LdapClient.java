package com.CADOPI.Project_CADOPI.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class LdapClient {

    @Autowired
    private Environment env;

    @Autowired
    private LdapTemplate ldapTemplate;

    public List<String> search(final String login) {
        List<List<String>> result = ldapTemplate.search(
                "", "(&(uid=" + login + ")(seeAlso=" + env.getRequiredProperty("ldap.member") + "*))",
                new PersonAttributesMapper());
        return (result.isEmpty() ? new ArrayList<>() : result.get(0));
    }

    private class PersonAttributesMapper implements AttributesMapper<List<String>> {
        @Override
        public List<String> mapFromAttributes(Attributes attrs) throws NamingException {
            List<String> list = new ArrayList<>();
            Attribute attr = attrs.get("seeAlso");
            NamingEnumeration e = attr.getAll();
            while (e.hasMore()) {
                String value = e.next().toString();
                // on recupère seulement les members de cadopi
                if (value.contains(env.getRequiredProperty("ldap.member"))) {
                    list.add(value);
                }
            }
            return list;
        }
    }

    public List<String> searchTraditional(final String login) {
        Hashtable envi = new Hashtable();
        envi.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        envi.put(Context.PROVIDER_URL, env.getRequiredProperty("ldap.urls") + env.getRequiredProperty("ldap.base.dn"));
        envi.put(Context.SECURITY_AUTHENTICATION, "simple");
        envi.put(Context.SECURITY_PRINCIPAL,
                env.getRequiredProperty("ldap.username"));
        envi.put(Context.SECURITY_CREDENTIALS, env.getRequiredProperty("ldap.password"));

        DirContext ctx;
        try {
            ctx = new InitialDirContext(envi);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        List<String> list = new LinkedList<>();
        NamingEnumeration results = null;
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = ctx.search("", "(&(uid=" + login + ")(seeAlso=" + env.getRequiredProperty("ldap.member") + "*))", controls);

            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute attr = attributes.get("seeAlso");
                NamingEnumeration e = attr.getAll();
                while (e.hasMore()) {
                    String value = e.next().toString();
                    // on recupère seulement les members de cadopi
                    if (value.contains(env.getRequiredProperty("ldap.member"))) {
                        list.add(value);
                    }
                }
            }
        } catch (NameNotFoundException e) {
            // The base context was not found.
            // Just clean up and exit.
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                    // Never mind this.
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                    // Never mind this.
                }
            }
        }
        return list;
    }
}
