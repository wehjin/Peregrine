package com.rubyhuntersky.peregrine;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;

/**
 * @author wehjin
 * @since 10/31/15.
 */

public class EtradeAccount {

    public String description;
    public String accountId;
    public String registrationType;
    public BigDecimal netAccountValue;

    public EtradeAccount(Element accountElement) {
        final NodeList childNodes = accountElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) item;
            final String tagName = element.getTagName();
            if ("accountDesc".equals(tagName)) {
                description = element.getTextContent();
            } else if ("netAccountValue".equals(tagName)) {
                netAccountValue = new BigDecimal(element.getTextContent());
            } else if ("accountId".equals(tagName)) {
                accountId = element.getTextContent();
            } else if ("registrationType".equals(tagName)) {
                registrationType = element.getTextContent();
            }
        }
    }

    public BigDecimal getNetAccountValue() {
        return netAccountValue;
    }

    @Override
    public String toString() {
        return "EtradeAccount{" +
              "description='" + description + '\'' +
              ", accountId='" + accountId + '\'' +
              ", registrationType='" + registrationType + '\'' +
              ", netAccountValue=" + netAccountValue +
              '}';
    }
}
