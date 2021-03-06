package com.example.acer.hello;



import android.util.Pair;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NodeTree {
    private String name;
    private NodeTree parent = null;
    private Map<String,NodeTree> children = new HashMap<>();
    private int uid = 0;

    public static Pair<List<BehaviorBean>,TreeNodeRoot> getBehaviorBeanList(String[] Behaviors){
        TreeNodeRoot root = new TreeNodeRoot();

        for(String str : Behaviors){
            String[] splited = str.split("/");
            //System.out.println(splited.length);
            NodeTree currNode = null;
            for(String segment : splited){
                if(!segment.isEmpty()){
                    //System.out.println(segment);
                    if(currNode == null){

                        if (root.contains(segment)){
                            currNode = root.get(segment);
                        }
                        else{
                            currNode = new NodeTree(segment,null);
                            root.put(segment, currNode);

                        }
                    }
                    else{
                        if(currNode.contains(segment)){
                            currNode = currNode.getChild(segment);
                        }
                        else{
                            currNode = currNode.addChild(segment);
                        }
                    }

                }//segment no empty
            }//for segment
        }//for str

        //System.out.println("**************************");

        List<BehaviorBean> behaviorBeenList = new ArrayList<BehaviorBean>();

        for(Map.Entry<String,NodeTree> entry : root.getEntrySet()){
            entry.getValue().traverse(behaviorBeenList);
        }

        return new Pair<List<BehaviorBean>,TreeNodeRoot>(behaviorBeenList,root);
    }

    public NodeTree(String name , @Nullable NodeTree parent){
        this.name = name;
        this.parent = parent;
        uid = uidGenerator.getInstance().getUid();
    }

    public boolean contains(String key){
        return children.containsKey(key);
    }

    public NodeTree getChild(String key){
        return children.get(key);
    }

    public NodeTree addChild(String name){
        NodeTree child = new NodeTree(name,this);
        children.put(name,child);
        return child;
    }

    public Map<String,NodeTree> getChildren(){
        return children;
    }

    public void traverse(int indent){
        String indentStr = "    ";
        String iStr = "";
        for(int i = 0 ; i < indent ;i++){
            iStr = iStr + indentStr;
        }
        System.out.println(iStr + "<" + name);
        for(Map.Entry<String, NodeTree> childEntery : children.entrySet()){
            childEntery.getValue().traverse(indent +1);
        }
        //System.out.println(iStr + name + ">");
    }

    public void traverse(@NotNull List<BehaviorBean> behaviorBeanList){
        behaviorBeanList.add(new BehaviorBean(uid,
                parent != null ? parent.getUid() : 0,name));
        for(Map.Entry<String, NodeTree> childEntery : children.entrySet()){
            childEntery.getValue().traverse(behaviorBeanList);
        }
    }

    public NodeTree traverse(String toMatch) {
        NodeTree toReturn = null;
        if(children.isEmpty()){
            if(name == toMatch){
                return this;
            }
        }
        for(Map.Entry<String, NodeTree> childEntery : children.entrySet()){
            NodeTree tmp = childEntery.getValue().traverse(toMatch);
            if(tmp != null){
                toReturn = tmp;
            }
        }
        return toReturn;
    }

    public String backTrace(String name){
        name = "/" + this.name + name;
        //System.out.println(name);
        if(parent != null){
            name = parent.backTrace(name);
        }
        return name;
    }

    public String backTraceGetFullName() throws Exception{
        if(!children.isEmpty()){
            throw new Exception("backTraceGetFullName Must be called by A leaf Node");
        }
        String bt = backTrace("");
        return bt;
    }

    public int getUid(){
        return uid;
    }
}


class uidGenerator{
    private int uid = 0;

    private static uidGenerator theInstance = new uidGenerator();

    public static uidGenerator getInstance(){
        return theInstance;
    }

    public int getUid(){
        uid++;
        return uid;
    }
}