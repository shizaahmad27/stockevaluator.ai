import React, { useEffect, useState } from 'react';
import { redditService, RedditPost, RedditComment, Ticker } from '../services/redditService';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Badge } from './ui/badge';
import { ScrollArea } from './ui/scroll-area';

export function RedditData() {
    const [posts, setPosts] = useState<RedditPost[]>([]);
    const [tickers, setTickers] = useState<Ticker[]>([]);
    const [selectedTicker, setSelectedTicker] = useState<string | null>(null);
    const [selectedPost, setSelectedPost] = useState<RedditPost | null>(null);
    const [comments, setComments] = useState<RedditComment[]>([]);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const [postsData, tickersData] = await Promise.all([
                redditService.getAllPosts(),
                redditService.getTrendingTickers()
            ]);
            setPosts(postsData);
            setTickers(tickersData);
        } catch (error) {
            console.error('Error loading data:', error);
        }
    };

    const handleTickerClick = async (ticker: string) => {
        setSelectedTicker(ticker);
        try {
            const posts = await redditService.getPostsByTicker(ticker);
            setPosts(posts);
        } catch (error) {
            console.error('Error loading posts for ticker:', error);
        }
    };

    const handlePostClick = async (post: RedditPost) => {
        setSelectedPost(post);
        try {
            const comments = await redditService.getCommentsByPostId(post.id);
            setComments(comments);
        } catch (error) {
            console.error('Error loading comments:', error);
        }
    };

    return (
        <div className="container mx-auto p-4">
            <Tabs defaultValue="tickers" className="w-full">
                <TabsList>
                    <TabsTrigger value="tickers">Trending Tickers</TabsTrigger>
                    <TabsTrigger value="posts">Reddit Posts</TabsTrigger>
                </TabsList>

                <TabsContent value="tickers">
                    <Card>
                        <CardHeader>
                            <CardTitle>Trending Tickers</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {tickers.map((ticker) => (
                                    <Card
                                        key={ticker.symbol}
                                        className="cursor-pointer hover:bg-accent"
                                        onClick={() => handleTickerClick(ticker.symbol)}
                                    >
                                        <CardContent className="p-4">
                                            <div className="flex justify-between items-center">
                                                <div>
                                                    <h3 className="font-bold">${ticker.symbol}</h3>
                                                    <p className="text-sm text-muted-foreground">
                                                        {ticker.companyName}
                                                    </p>
                                                </div>
                                                <div className="text-right">
                                                    <p className="font-semibold">
                                                        Mentions: {ticker.mentionCount}
                                                    </p>
                                                    <p className="text-sm">
                                                        Sentiment: {ticker.sentimentScore.toFixed(2)}
                                                    </p>
                                                </div>
                                            </div>
                                        </CardContent>
                                    </Card>
                                ))}
                            </div>
                        </CardContent>
                    </Card>
                </TabsContent>

                <TabsContent value="posts">
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                        <Card>
                            <CardHeader>
                                <CardTitle>
                                    {selectedTicker
                                        ? `Posts mentioning $${selectedTicker}`
                                        : 'Recent Posts'}
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <ScrollArea className="h-[600px]">
                                    {posts.map((post) => (
                                        <Card
                                            key={post.id}
                                            className="mb-4 cursor-pointer hover:bg-accent"
                                            onClick={() => handlePostClick(post)}
                                        >
                                            <CardContent className="p-4">
                                                <h3 className="font-bold mb-2">{post.title}</h3>
                                                <p className="text-sm text-muted-foreground mb-2">
                                                    by u/{post.author} • {post.score} points •{' '}
                                                    {post.commentsCount} comments
                                                </p>
                                                <p className="text-sm mb-2 line-clamp-3">
                                                    {post.content}
                                                </p>
                                                <div className="flex flex-wrap gap-2">
                                                    {post.tickerMentions.map((ticker) => (
                                                        <Badge
                                                            key={ticker}
                                                            variant="secondary"
                                                            className="cursor-pointer"
                                                            onClick={(e) => {
                                                                e.stopPropagation();
                                                                handleTickerClick(ticker);
                                                            }}
                                                        >
                                                            ${ticker}
                                                        </Badge>
                                                    ))}
                                                </div>
                                            </CardContent>
                                        </Card>
                                    ))}
                                </ScrollArea>
                            </CardContent>
                        </Card>

                        {selectedPost && (
                            <Card>
                                <CardHeader>
                                    <CardTitle>Comments</CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <ScrollArea className="h-[600px]">
                                        {comments.map((comment) => (
                                            <Card key={comment.id} className="mb-4">
                                                <CardContent className="p-4">
                                                    <p className="text-sm text-muted-foreground mb-2">
                                                        by u/{comment.author} • {comment.score} points
                                                    </p>
                                                    <p className="text-sm mb-2">{comment.content}</p>
                                                    {comment.tickerMentions.length > 0 && (
                                                        <div className="flex flex-wrap gap-2">
                                                            {comment.tickerMentions.map((ticker) => (
                                                                <Badge
                                                                    key={ticker}
                                                                    variant="secondary"
                                                                    className="cursor-pointer"
                                                                    onClick={() =>
                                                                        handleTickerClick(ticker)
                                                                    }
                                                                >
                                                                    ${ticker}
                                                                </Badge>
                                                            ))}
                                                        </div>
                                                    )}
                                                </CardContent>
                                            </Card>
                                        ))}
                                    </ScrollArea>
                                </CardContent>
                            </Card>
                        )}
                    </div>
                </TabsContent>
            </Tabs>
        </div>
    );
} 