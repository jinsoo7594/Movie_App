package com.songspagetti.moivemvvm.ui.popular_movie

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.songspagetti.moivemvvm.R
import com.songspagetti.moivemvvm.data.api.POSTER_BASE_URL
import com.songspagetti.moivemvvm.data.repository.NetworkState
import com.songspagetti.moivemvvm.data.vo.Movie
import com.songspagetti.moivemvvm.ui.single_movie_details.SingleMovie
import kotlinx.android.synthetic.main.movie_list_item.view.*
import kotlinx.android.synthetic.main.network_state_item.view.*


class PopularMoviePagedListAdapter (private val context: Context) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {
    // these guys will help me decide what types of view to show in the recycler view
    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View

        if(viewType == MOVIE_VIEW_TYPE){
            view = layoutInflater.inflate(R.layout.movie_list_item, parent, false)
            return MovieItemViewHolder(view)
        }else{
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == MOVIE_VIEW_TYPE){
            (holder as MovieItemViewHolder).bind(getItem(position), context)
        }
        else{
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow(): Boolean{
        // when networkstate is loading or error
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int{
        // this will returns the total items in the list
        // and this will help us add an extra item which is the progress bar at the end
        return super.getItemCount() + if(hasExtraRow()) 1 else 0
    }
    //If we have 20 items, the last position of the item will be 19 (0~19)
    //but the itemCount will be 20 so here we need to minus 1 for the last position
    // As hasExtraRow() is true, we are increasing the itemCount by 1 so we have 21 items including the progressbar
    // but the position of the last item is 20 so if hasExtraRow() is true and the position is the last position we return network view type
    override fun getItemViewType(position: Int): Int{
        return if(hasExtraRow() && position == itemCount -1){ // getItemCount() == itemCount
            NETWORK_VIEW_TYPE
        }else{
            MOVIE_VIEW_TYPE
        }
    }



    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>(){
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }
    //Our recyclerview has two types of views.
    //One is to show the MovieListItem and the other is to show the NetworkStateItem
    //So I need to create thease two view holder classes
    class MovieItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bind(movie: Movie?, context: Context){
            itemView.cv_movie_title.text = movie?.title
            itemView.cv_movie_release_date.text = movie?.releaseDate
            // ㄴ onCLickListener 달아줘야한다. (클릭시 Single movie activity로 이동)

            val moviePosterURL: String = POSTER_BASE_URL + movie?.posterPath
            Glide.with(itemView.context)
                .load(moviePosterURL)
                .into(itemView.cv_iv_movie_poster)

            itemView.setOnClickListener {
                val intent = Intent(context, SingleMovie::class.java)
                intent.putExtra("id", movie?.id)
                context.startActivity(intent)
            }
        }
    }

    class NetworkStateItemViewHolder (view: View) : RecyclerView.ViewHolder(view){
        fun bind(networkState: NetworkState?){
            if(networkState != null && networkState == NetworkState.LOADING){
                itemView.progress_bar_item.visibility = View.VISIBLE
            }
            else{
                itemView.progress_bar_item.visibility = View.GONE
            }
            if(networkState != null && networkState == NetworkState.ERROR){
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.msg
            }
            else if(networkState != null && networkState == NetworkState.ENDOFLIST){
                itemView.error_msg_item.visibility = View.VISIBLE
                itemView.error_msg_item.text = networkState.msg
            }else{
                itemView.error_msg_item.visibility = View.GONE
            }
        }
    }

    fun setNetworkState(newNetworkState: NetworkState){
        val previousState: NetworkState? = this.networkState // current value of networkState is in this previousState variable
        val hadExtraRow:Boolean = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow: Boolean = hasExtraRow()

        if(hadExtraRow != hasExtraRow){
            if(hadExtraRow){                             //hadExtraRow is true and hasExtraRow is false. It means we don't have any new movies.
                notifyItemRemoved(super.getItemCount())  //remove the progressbar at the end
            } else {                                     //hasExtraRow is true and hadExtraRow is false
                notifyItemInserted(super.getItemCount()) //add the progressbar at the end
            }
        }else if(hasExtraRow && previousState != newNetworkState){ //hasExtraRow is true and hadExtraRow is true. It means we have an error
            notifyItemChanged(itemCount -1)
        }

    }


}