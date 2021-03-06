package com.example.acer.hello;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

public abstract class TreeListViewAdapter<T> extends BaseAdapter
{

    protected Context mContext;
    /**
     * 存储所有可见的Node
     */
    protected List<Node> mNodes;
    protected LayoutInflater mInflater;
    /**
     * 存储所有的Node
     */
    protected List<Node> mAllNodes;

    public List<Node> getmNodes(){
        return mNodes;
    }

    /**
     * 点击的回调接口
     */
    private OnTreeNodeClickListener onTreeNodeClickListener;

    public interface OnTreeNodeClickListener
    {
        void onClick(Node node, int position);
    }

    public void setOnTreeNodeClickListener(
            OnTreeNodeClickListener onTreeNodeClickListener)
    {
        this.onTreeNodeClickListener = onTreeNodeClickListener;
    }

    /**
     *
     * @param mTree
     * @param context
     * @param mNodes
     * @param mAllNodes
     * @param defaultExpandLevel
     *            默认展开几级树
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public TreeListViewAdapter(ListView mTree, Context context, List<Node> mNodes,
                               List<Node> mAllNodes,int defaultExpandLevel) throws IllegalArgumentException,
            IllegalAccessException
    {
        mContext = context;
        /**
         * 对所有的Node进行排序
         */
        this.mNodes = mNodes;
        this.mAllNodes = mAllNodes;
        //mAllNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);
        /**
         * 过滤出可见的Node
         */
        this.mNodes = TreeHelper.filterVisibleNode(mAllNodes);
        mInflater = LayoutInflater.from(context);

        /**
         * 设置节点点击时，可以展开以及关闭；并且将ItemClick事件继续往外公布
         */
        mTree.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                System.out.println("onItemClick:"+position);
                expandOrCollapse(position);
            }

        });

    }

    /**
     * 相应ListView的点击事件 展开或关闭某节点
     *
     * @param position
     */
    public void expandOrCollapse(int position)
    {
        Node n = mNodes.get(position);
        System.out.println(n.getName());
        if (n != null)// 排除传入参数错误异常
        {
            if (!n.isLeaf())
            {
                System.out.println("n is not leaf");
                n.setExpand(!n.isExpand());
                mNodes = TreeHelper.filterVisibleNode(mAllNodes);
                notifyDataSetChanged();// 刷新视图
            }
            else{
                if (onTreeNodeClickListener != null)
                {
                    System.out.println("onTreeNodeClickListener is not null");
                    onTreeNodeClickListener.onClick(getmNodes().get(position),
                            position);
                }
            }
        }
    }

    @Override
    public int getCount()
    {
        return mNodes.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mNodes.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        Node node = mNodes.get(position);
        convertView = getConvertView(node, position, convertView, parent);

        // 设置内边距
        convertView.setPadding(node.getLevel() * 30, 3, 3, 3);
        return convertView;
    }

    public abstract View getConvertView(Node node, int position,
                                        View convertView, ViewGroup parent);

}
