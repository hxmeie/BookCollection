package com.hxm.books.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hxm.books.R;
import com.hxm.books.activity.BookshelfFragment;
import com.hxm.books.bean.Book;
import com.hxm.books.utils.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import java.util.List;

/**
 * Created by hxm on 2016/2/1.
 */
public class BookShelfAdapter extends BaseAdapter {
    private Context context;
    private List<Book> mBook;
    private DisplayImageOptions options;
    private ImageLoadingListener imageLoadingListener = new BookshelfFragment.FirstDisplayListener();

    public BookShelfAdapter(Context context,List<Book> mBook){
        this.context=context;
        this.mBook=mBook;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.no_cover)
                .showImageForEmptyUri(R.mipmap.no_cover)
                .showImageOnFail(R.mipmap.no_cover)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .displayer(new RoundedBitmapDisplayer(20))
//                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }

    @Override
    public int getCount() {
        return mBook.size();
    }

    @Override
    public Object getItem(int position) {
        return mBook.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.bookshelf_list_item,parent,false);
        }
        ImageView bookPic= ViewHolder.get(convertView,R.id.iv_bookshelf_item_bookpic);
        TextView bookName=ViewHolder.get(convertView,R.id.tv_bookshelf_item_bookname);
        TextView bookSummary=ViewHolder.get(convertView,R.id.tv_bookshelf_item_summary);
        TextView bookAuthor=ViewHolder.get(convertView,R.id.tv_bookshelf_item_bookauthor);
        TextView bookType1=ViewHolder.get(convertView,R.id.tv_bookshelf_item_type_1);
        TextView bookType2= ViewHolder.get(convertView, R.id.tv_bookshelf_item_type_2);

        Book book = (Book) getItem(position);
        ImageLoader.getInstance().displayImage(book.getBookImage(),bookPic,options,imageLoadingListener);
        bookName.setText(book.getTitle());
        bookSummary.setText(book.getSummary());
        bookAuthor.setText(book.getAuthor());
        bookType1.setText(book.getTag1());
        bookType2.setText(book.getTag2());
        return convertView;
    }

    }

    //传统ViewHolder写法，用ViewHolder类替代
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView==null){
//            convertView=LayoutInflater.from(context).inflate(R.layout.bookshelf_list_item,null);
//            holder=new ViewHolder();
//            holder. bookPic= (ImageView) convertView.findViewById(R.id.iv_bookshelf_item_bookpic);
//            holder. bookName=(TextView) convertView.findViewById(R.id.tv_bookshelf_item_bookname);
//            holder. bookSummary=(TextView) convertView.findViewById(R.id.tv_bookshelf_item_summary);
//            holder. bookAuthor=(TextView) convertView.findViewById(R.id.tv_bookshelf_item_bookauthor);
//            holder. bookType1=(TextView) convertView.findViewById(R.id.tv_bookshelf_item_type_1);
//            holder. bookType2=(TextView) convertView.findViewById(R.id.tv_bookshelf_item_type_2);
//            convertView.setTag(holder);
//        }
//        holder= (ViewHolder) convertView.getTag();
//        KJBitmap bookImage=new KJBitmap();
//        bookImage.display(holder.bookPic, mBook.get(position).getBookImage());
//        holder.bookName.setText(mBook.get(position).getTitle());
//        holder.bookSummary.setText(mBook.get(position).getSummary());
//        holder.bookAuthor.setText(mBook.get(position).getAuthor());
//        holder.bookType1.setText(mBook.get(position).getTag1());
//        holder.bookType2.setText(mBook.get(position).getTag2());
//        return convertView;
//    }
//
//    public class ViewHolder{
//        ImageView bookPic;
//        TextView bookName;
//        TextView bookSummary;
//        TextView bookAuthor;
//        TextView bookType1;
//        TextView bookType2;
//    }
//}
