import * as fs from 'fs';

const words = fs.readFileSync(process.argv[2] || '', 'utf-8').toLowerCase().match(/[a-z]{2,}/g)|| [];
const delThg = words.filter(w => !fs.readFileSync('../stop_words.txt', 'utf-8').split(',').includes(w));
const wordCnt = delThg.reduce((acc: Record<string, number>, w) => { acc[w] = (acc[w] ?? 0) + 1; return acc }, {});
Object.entries(wordCnt || {}).sort((a, b) => b[1] - a[1]).slice(0, 25).forEach(r => console.log(r[0], ' - ', r[1]));