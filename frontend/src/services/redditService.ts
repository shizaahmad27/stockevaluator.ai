import { useGetTrendingTickers, useGetAllPosts, useGetPostsByTicker, useGetCommentsByPostId } from '@/generated/api/reddit-controller';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export interface RedditPost {
    id: string;
    title: string;
    content: string;
    author: string;
    score: number;
    commentsCount: number;
    createdDate: string;
    tickerMentions: string[];
}

export interface RedditComment {
    id: string;
    postId: string;
    content: string;
    author: string;
    score: number;
    createdDate: string;
    tickerMentions: string[];
}

export interface Ticker {
    symbol: string;
    companyName: string;
    mentionCount: number;
    sentimentScore: number;
    trendingScore: number;
}

export const useRedditService = () => {
    const { data: trendingTickers, isLoading: isLoadingTickers } = useGetTrendingTickers();
    const { data: allPosts, isLoading: isLoadingPosts } = useGetAllPosts();
    const { data: postsByTicker, isLoading: isLoadingPostsByTicker } = useGetPostsByTicker();
    const { data: commentsByPost, isLoading: isLoadingComments } = useGetCommentsByPostId();

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

export const redditService = {
    async getAllPosts(): Promise<RedditPost[]> {
        const response = await axios.get(`${API_URL}/reddit/posts`);
        return response.data;
    },

    async getPostsByTicker(ticker: string): Promise<RedditPost[]> {
        const response = await axios.get(`${API_URL}/reddit/posts/${ticker}`);
        return response.data;
    },

    async getCommentsByPostId(postId: string): Promise<RedditComment[]> {
        const response = await axios.get(`${API_URL}/reddit/comments/${postId}`);
        return response.data;
    },

    async getAllTickers(): Promise<Ticker[]> {
        const response = await axios.get(`${API_URL}/reddit/tickers`);
        return response.data;
    },

    async getTrendingTickers(): Promise<Ticker[]> {
        const response = await axios.get(`${API_URL}/reddit/tickers/trending`);
        return response.data;
    }
}; 