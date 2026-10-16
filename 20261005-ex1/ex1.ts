import * as fs from 'fs';

// get the param from the command
const filePath = process.argv[2];

// get the book and change to lower case
const fullText = fs.readFileSync(filePath || '', 'utf-8').trim().toLowerCase();

// split with enter(change line), symbols, and underscores
const deleteSymbols = fullText.split(/[\W_]+/);

// delete stop words
let stopWords = new Set(fs.readFileSync('../stop_words.txt', 'utf-8').split(','));
const deleteNotNecess = deleteSymbols.filter(el => !/^\d+$/.test(el) && el.length > 1 && !stopWords.has(el));

const wordCount: Record<string, number> = {};
for (const w of deleteNotNecess) {
    wordCount[w] = (wordCount[w] ?? 0) + 1;
}

const sortWrdCnt = Object.entries(wordCount).sort((a, b) => b[1] - a[1]).slice(0, 25);
sortWrdCnt.forEach(el => console.log(el[0], ' - ', el[1]));