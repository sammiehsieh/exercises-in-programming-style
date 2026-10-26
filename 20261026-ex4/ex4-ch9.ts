import * as fs from 'fs';

type Func = (data: any, next: Func) => void;


function readFile(pathToFile: string, func: Func): void {
    const data = fs.readFileSync(pathToFile, 'utf-8');
    func(data, normalize);
}

function filterChars(strData: string, func: Func): void {
    func(strData.replace(/[\W_]+/g, ' '), scan);
}

function normalize(strData: string, func: Func): void {
    func(strData.toLowerCase(), removeStopWords);
}

function scan(strData: string, func: Func): void {
    func(strData.trim().split(/\s+/), frequencies);
}

function removeStopWords(wordList: string[], func: Func): void {
  const stopWords = fs.readFileSync('../stop_words.txt', 'utf-8').trim().split(',');
  func(wordList.filter((w) => w.length > 1 && !stopWords.includes(w)), sort);
}

function frequencies(wordList: string[], func: Func): void {
    const wordFreq: Record<string, number> = {};
    for (const w of wordList) {
        wordFreq[w] = (wordFreq[w] ?? 0) + 1;
    }
    func(wordFreq, printText);
}

function sort(wf: Record<string, number>, func: Func): void {
    func(Object.entries(wf).sort((a, b) => b[1] - a[1]), noOp);
}

function printText(wordFreqs: [string, number][], func: Func): void {
    for (const [w, c] of wordFreqs.slice(0, 25)) {
        console.log(w, ' - ', c);
    }
    func(null, noOp);
}

function noOp(_func?: any): void {
    return;
}

readFile(process.argv[2] || '', filterChars);