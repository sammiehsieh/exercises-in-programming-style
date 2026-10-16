import * as fs from 'fs';

const words = fs.readFileSync(process.argv[2] || '', 'utf-8').toLowerCase().match(/[a-z]{2,}/g);
const delThg = words?.filter(w => !fs.readFileSync('../stop_words.txt', 'utf-8').split(',').includes(w));
const wordCount: Record<string, number> = {};
delThg?.forEach(w => wordCount[w] = (wordCount[w] ?? 0) + 1);
const result = Object.entries(wordCount).sort((a, b) => b[1] - a[1]).slice(0, 25);
result.forEach(r => console.log(r[0], ' - ', r[1]));