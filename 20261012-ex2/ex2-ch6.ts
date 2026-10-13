import * as fs from 'fs';

function readFile(path: string) {
    return fs.readFileSync(path, 'utf-8');
}

function filterCharsAndNormalize(strData: string) {
    let result = '';
    for (const s of strData) {
        if (/[\W_]/.test(s)) {
            result += ' ';
        } else {
            result += s.toLowerCase();
        }
    }
    return result;
}

function scan(strData: string) {
    return strData.split(/\s+/);
}

function removeStopWords(wordList: string[]) {
    const stopWords = fs.readFileSync('../stop_words.txt', 'utf-8').split(',');
    let result: string[] = [];
    for (const w of wordList) {
        if (!stopWords.includes(w) && w.length !== 1) {
            result.push(w);
        }
    }
    return result;
}

function frequencies(wordList: string[]) {
    let wordFreqs: { [key: string]: number } = {};
    for (const w of wordList) {
        if (wordFreqs[w]) {
            wordFreqs[w] += 1;
        } else {
            wordFreqs[w] = 1;
        }
    }
    return wordFreqs;
}

function sort(wordFreq: { [key: string]: number }) {
    const keys = Object.keys(wordFreq);
    let result: [string, number][] = [];
    for (let i = 0; i < keys.length; i++) {
        result.push([
            keys[i]!, wordFreq[keys[i]!] ?? 0
        ]);
    }
    return result.sort((a, b) => b[1] - a[1]);
}

function consoleAll(wordFreq: [string, number][]) {
    if (wordFreq.length > 0) {
        console.log(wordFreq[0]![0], '-', wordFreq[0]![1]);
        consoleAll(wordFreq.slice(1));
    }
}

consoleAll(sort(frequencies(removeStopWords(scan(filterCharsAndNormalize(readFile(process.argv[2] || '')))))).slice(0, 25));