import React, { useEffect, useState } from 'react';
import { Card, CardContent, Typography, Grid, TextField, Button } from '@mui/material';
import axios from 'axios';

interface WSBPost {
  id: string;
  title: string;
  score: number;
  num_comments: number;
  created_utc: number;
  url: string;
  permalink: string;
}

const WSBFeed = () => {
  const [posts, setPosts] = useState<WSBPost[]>([]);
  const [searchTicker, setSearchTicker] = useState('');
  const [loading, setLoading] = useState(false);

  const fetchHotPosts = async () => {
    try {
      setLoading(true);
      const response = await axios.get('http://localhost:8080/api/reddit/hot');
      setPosts(response.data.map((post: any) => ({
        id: post.id,
        title: post.title,
        score: post.score,
        num_comments: post.num_comments,
        created_utc: post.created_utc,
        url: post.url,
        permalink: `https://reddit.com${post.permalink}`
      })));
    } catch (error) {
      console.error('Error fetching WSB posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const searchByTicker = async () => {
    if (!searchTicker) return;
    try {
      setLoading(true);
      const response = await axios.get(`http://localhost:8080/api/reddit/search/${searchTicker}`);
      setPosts(response.data.map((post: any) => ({
        id: post.id,
        title: post.title,
        score: post.score,
        num_comments: post.num_comments,
        created_utc: post.created_utc,
        url: post.url,
        permalink: `https://reddit.com${post.permalink}`
      })));
    } catch (error) {
      console.error('Error searching posts:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHotPosts();
  }, []);

  return (
    <div style={{ padding: '20px' }}>
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Typography variant="h4" gutterBottom>
            WallStreetBets Feed
          </Typography>
          <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
            <TextField
              label="Search by Ticker"
              variant="outlined"
              value={searchTicker}
              onChange={(e) => setSearchTicker(e.target.value.toUpperCase())}
              placeholder="e.g., GME"
            />
            <Button
              variant="contained"
              onClick={searchByTicker}
              disabled={loading || !searchTicker}
            >
              Search
            </Button>
            <Button
              variant="outlined"
              onClick={fetchHotPosts}
              disabled={loading}
            >
              Show Hot Posts
            </Button>
          </div>
        </Grid>
        {posts.map((post) => (
          <Grid item xs={12} key={post.id}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {post.title}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  Score: {post.score} | Comments: {post.num_comments}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  Posted: {new Date(post.created_utc * 1000).toLocaleString()}
                </Typography>
                <Typography variant="body2">
                  <a href={post.permalink} target="_blank" rel="noopener noreferrer">
                    View on Reddit
                  </a>
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </div>
  );
};

export default WSBFeed; 