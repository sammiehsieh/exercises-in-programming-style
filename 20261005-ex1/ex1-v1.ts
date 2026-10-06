import * as fs from 'fs';

const filePath = process.argv[2];

// get the book and change to lower case
const fullText = fs.readFileSync(filePath || '', 'utf-8').trim().toLowerCase();

// delete change line
const deleteEnter = fullText.split(/\n/).join(' ');

// split with space
const splitSpace = deleteEnter.split(/\ /).map(el => el.trim()).filter(el => el.length > 0);

// delete symbols
const symbolSplit = splitSpace.flatMap(el => el.split(/\W/g)).flatMap(el => el.split(/\_/g)).filter(el => el.length > 0);

// delete numbers
const deleteNumbers = symbolSplit.filter(el => !/^\d+$/.test(el));

// delete stop words
let stopWords = fs.readFileSync('../stop_words.txt', 'utf-8').split(',');
for (const abc of 'abcdefghijklmnopqrstuvwxyz') {
    stopWords.push(abc);
}
const deleteStopWord = deleteNumbers.filter(el => !stopWords.some(el2 => el === el2)).sort();
// console.log(deleteStopWord);

// *** this is not working ***
// array to object with no duplicates
// const allWordsObj = Object.fromEntries([...new Set(deleteStopWord)].map(key => [key, 0]));
// for (const w of deleteStopWord) {
//     allWordsObj[w]++;
// }
// console.log(allWordsObj);

const wordCount: Record<string, number> = {};
for (const w of deleteStopWord) {
    wordCount[w] = (wordCount[w] ?? 0) + 1;
}

const sortWrdCnt = Object.entries(wordCount).sort((a, b) => b[1] - a[1]).filter((e, i) => i <= 24);
const result = Object.fromEntries(sortWrdCnt)
console.log(result);