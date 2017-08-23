package com.workingbit.coremodule.domain.impl

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators

/**
 * Created by Aleksey Popryaduhin on 18:38 23/08/2017.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator::class)
data class TreeNode<T>(val data: T?) {
    var parent: TreeNode<T>? = null
    val children: MutableList<TreeNode<T>> = mutableListOf()

    fun addChild(child: TreeNode<T>) {
        child.parent = this
        children.add(child)
    }

    @JsonIgnore
    fun getRootTree(): TreeNode<T> {
        val parent: TreeNode<T>? = this.parent
        if (parent != null && parent.parent == null) {
            return parent
        }
        return parent!!.getRootTree()
    }

    fun breadthFirstIter(): Iterator<TreeNode<T>> {
        var queue = mutableListOf<TreeNode<T>>()
        var root = getRootTree()
        return object : Iterator<TreeNode<T>> {
            override fun hasNext(): Boolean = !queue.isEmpty()

            override fun next(): TreeNode<T> {
                val node = queue.removeAt(0)
                queue.addAll(node.children)
                return node
            }
        }
    }
}