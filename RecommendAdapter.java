package com.example.chirag.librarybooklocator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chirag.librarybooklocator.Crawler.Book;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by effy on 2018/4/8.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendHolder> {
    private final int LAST_SEARCH = 0;
    private final int RECOMMEND = 1;
    private LayoutInflater mInflater;
    private int dateSize = 8;
    String[] date = new String[dateSize];
    ArrayList<String> dateList = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private LruCache<String, BitmapDrawable> mMemoryCache;


    ArrayList<Book> bookList = new ArrayList<>();

    String accountNo;


    public RecommendAdapter(Context context, String accountNo, ArrayList<Book> bookList) {
        this.mInflater = LayoutInflater.from(context);
        this.accountNo = accountNo;
        this.bookList = bookList;
        setRcm_date();


        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable drawable) {
                return drawable.getBitmap().getByteCount();
            }
        };
    }

    @Override
    public RecommendAdapter.RecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = mInflater.inflate(R.layout.daily_recommend, parent, false);


        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, (int) v.getTag());
                }

            }
        });
        return new RecommendHolder(item);
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0 && bookList.size() == 8) {
            return LAST_SEARCH;
        }
        return RECOMMEND;
        //return position;
    }

    @Override
    public void onBindViewHolder(RecommendAdapter.RecommendHolder holder, int position) {


        Book book = bookList.get(position);

        if (bookList.size() == 8) {

            holder.tv_time.setText(dateList.get(position));

        } else {
            holder.tv_time.setText(dateList.get(position + 1));

        }


        holder.itemView.setTag(position);


        holder.tv_bookname.setText(book.getTitle());
        holder.tv_authorname.setText(book.getAuthorName());

        String url = book.getimageLink();
        if (!url.contains("http") || url.equals("") || url.isEmpty() || url == null || url.equals("not found")) {
            url = "http://www.51allout.co.uk/wp-content/uploads/2012/02/Image-not-found.gif";
            // url="https://upload.wikimedia.org/wikipedia/commons/a/ac/No_image_available.svg";
        }


        BitmapDrawable drawable = getBitmapFromMemoryCache(url);
        if (drawable != null) {
            holder.imageView.setImageDrawable(drawable);
        } else {


            BitmapWorkerTask task = new BitmapWorkerTask(holder.imageView);
            task.execute(url);
        }


    }


    @Override
    public int getItemCount() {
        return bookList.size();
    }

    private void setRcm_date() {
        date[0] = "Last time";
        dateList.add(0, "THE BOOK SEARCHED BEFORE");


        dateList.add(1, "Today's Recommendation");

        dateList.add(2, "Yesterday's Recommendation");
        Calendar calendar = Calendar.getInstance();
        String temp = new String();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -1);
        for (int i = 3; i < dateSize; i++) {
            calendar.add(Calendar.DATE, -1);
            temp = dateFormat.format(calendar.getTime());
            dateList.add(i, temp);
        }
    }

    public void changeItem(int position, Book book) {
        bookList.remove(0);
        bookList.add(0,book);
        notifyDataSetChanged();

    }

    public void addItem(int position,Book book) {
            bookList.add(0,book);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
    }


    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addBitmapToMemoryCache(String key, BitmapDrawable drawable) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, drawable);
        }
    }

    public BitmapDrawable getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public class RecommendHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tv_time;
        private TextView tv_bookname;
        private TextView tv_authorname;
        private View view;

        public RecommendHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_bookname = itemView.findViewById(R.id.tv_bookname);
            tv_authorname = itemView.findViewById(R.id.tv_authorname);
            view = itemView;
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, BitmapDrawable> {

        private ImageView mImageView;

        public BitmapWorkerTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected BitmapDrawable doInBackground(String... params) {
            String imageUrl = params[0];

            Bitmap bitmap = downloadBitmap(imageUrl);

            BitmapDrawable drawable = new BitmapDrawable(mInflater.getContext().getResources(), bitmap);
            addBitmapToMemoryCache(imageUrl, drawable);
            return drawable;
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            if (mImageView != null && drawable != null) {
                mImageView.setImageDrawable(drawable);
            }

        }

        private Bitmap downloadBitmap(String imageUrl) {

            Bitmap bitmap = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(10 * 1000);
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }

            return bitmap;
        }

    }

}
