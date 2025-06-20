import { 
    useGetTrendingTickers, 
    useGetRecentPosts, 
    useGetPostsByTicker, 
    useGetCommentsByPost,
    getTrendingTickers,
    getRecentPosts,
    getPostsByTicker,
    getCommentsByPost
} from '@/api/generated/reddit-controller/reddit-controller';
import type { RedditPost, RedditComment, Ticker } from '@/api/generated/model';

// React Query hooks for components (with caching, loading states, etc.)
export const useRedditService = () => {
    const { data: trendingTickers, isLoading: isLoadingTickers } = useGetTrendingTickers();
    const { data: allPosts, isLoading: isLoadingPosts } = useGetRecentPosts();
    const { data: postsByTicker, isLoading: isLoadingPostsByTicker } = useGetPostsByTicker('');
    const { data: commentsByPost, isLoading: isLoadingComments } = useGetCommentsByPost('');

    return {
        trendingTickers,
        allPosts,
        postsByTicker,
        commentsByPost,
        isLoadingTickers,
        isLoadingPosts,
        isLoadingPostsByTicker,
        isLoadingComments
    };
};

// Direct API functions for programmatic use (no caching, just raw API calls)
export const redditService = {
    async getAllPosts(): Promise<RedditPost[]> {
        return await getRecentPosts();
    },

    async getPostsByTicker(ticker: string): Promise<RedditPost[]> {
        return await getPostsByTicker(ticker);
    },

    async getCommentsByPostId(postId: string): Promise<RedditComment[]> {
        return await getCommentsByPost(postId);
    },

    async getTrendingTickers(): Promise<Ticker[]> {
        return await getTrendingTickers();
    }
};
