package com.rubyhuntersky.peregrine.utility

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.util.*

/**
 * @author Jeffrey Yu
 * @since 2/15/17.
 */

val Element.childNodesAsList: List<Node>
    get() {
        val nodeList: NodeList = childNodes
        return (0..nodeList.length - 1).map { nodeList.item(it) }
    }

val Element.childElements: List<Element>
    get() = childNodesAsList.filter { it.nodeType == Node.ELEMENT_NODE }.map { it as Element }

fun Element.getChildElement(name: String): Element {
    return childElements.firstOrNull { name == it.tagName } ?: throw NoSuchElementException(name)
}
