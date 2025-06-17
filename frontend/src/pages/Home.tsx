import { RedditData } from '../components/RedditData';

export function Home() {
    return (
        <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold mb-8">Stock Evaluator</h1>
            <RedditData />
        </div>
    );
} 