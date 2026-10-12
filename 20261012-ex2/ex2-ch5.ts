import * as fs from 'fs';

let data: any[] = [];
let words: any[] = [];
let wordFreqs: any[] = [];

function readFile(path: string): void {
    data = [...data, ...fs.readFileSync(path, 'utf-8')];
}

function filterCharsAndNormalize(): void {
    for (let i = 0; i < data.length; i++) {
        if (!/[a-zA-Z0-9]/.test(data[i])) {
            data[i] = ' ';
        } else {
            data[i] = data[i].toLowerCase();
        }
    }
}

function scan() {
    let dataStr = data.join('');
    words = [...words, ...dataStr.split(/\s+/)];
}

function removeStopWords() {
    let stopWords = fs.readFileSync('../stop_words.txt', 'utf-8').split(',');
    for (const abc of 'abcdefghijklmnopqrstuvwxyz') {
        stopWords.push(abc);
    }
    let indexes: number[] = [];
    for (let i = 0; i < words.length; i++) {
        if (stopWords.includes(words[i])) {
            indexes.push(i);
        }
    }
    for (let i = indexes.length - 1; i >= 0; i--) {
        words = [...words.slice(0, indexes[i]), ...words.slice(indexes[i]! + 1, words.length)];
    }
}

function frequencies() {
    for (const w of words) {
        let keys: any[] = [];
        for (const wd of wordFreqs) {
            keys.push(wd[0]);
        }
        if (keys.includes(w)) {
            wordFreqs[keys.indexOf(w)][1] += 1;
        } else {
            wordFreqs.push([w, 1]);
        }
    }
}

function sort() {
    wordFreqs.sort((a, b) => b[1] - a[1]);
}

readFile(process.argv[2] || '');
filterCharsAndNormalize();
scan();
removeStopWords();
frequencies();
sort();

for (const tf of wordFreqs.slice(0, 25)) {
    console.log(tf[0], '-', tf[1]);
}