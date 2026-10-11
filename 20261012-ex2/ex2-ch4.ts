import * as fs from 'fs';

let wordFreqs: [string, number][] = [];

let stopWords = fs.readFileSync('../stop_words.txt', 'utf-8').split(',');
for (const abc of 'abcdefghijklmnopqrstuvwxyz') {
    stopWords.push(abc);
}

for (let line of fs.readFileSync(process.argv[2] || '', 'utf-8').split(/\n/)) {
    line += '\n';
    let startChar: number | null = null;
    let i = 0;
    for (const c of line) {
        if (startChar === null) {
            if (/[a-zA-Z0-9]/.test(c)) {
                startChar = i;
            }
        } else {
            if (!/[a-zA-Z0-9]/.test(c)) {
                let found = false;
                let word = line.slice(startChar, i).toLowerCase();
                if (!stopWords.includes(word)) {
                    let pairIndex = 0;
                    for (let pair of wordFreqs) {
                        if (word === pair[0]) {
                            pair[1] += 1;
                            found = true;
                            break;
                        }
                        pairIndex += 1;
                    }
                    if (!found) {
                        wordFreqs.push([word, 1]);
                    } else if (wordFreqs.length > 1) {
                        for (let n = pairIndex - 1; n >= 0; n--) {
                            if (wordFreqs[pairIndex]![1] > wordFreqs[n]![1]) {
                                [wordFreqs[n], wordFreqs[pairIndex]] = [wordFreqs[pairIndex]!, wordFreqs[n]!];
                                pairIndex = n;
                            }
                        }
                    }
                }
                startChar = null;
            }
        }
        i += 1;
    }
}

for (const tf of wordFreqs.slice(0, 25)) {
    console.log(tf[0], '-', tf[1]);
}

