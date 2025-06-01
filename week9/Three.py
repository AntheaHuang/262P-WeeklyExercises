import sys, string
import numpy as np

# Example input: "Hello  World!" 
characters = np.array([' ']+list(open(sys.argv[1]).read())+[' '])
# Result: array([' ', 'H', 'e', 'l', 'l', 'o', ' ', ' ', 
#           'W', 'o', 'r', 'l', 'd', '!', ' '], dtype='<U1')

# Normalize: Convert to uppercase
characters[~np.char.isalpha(characters)] = ' '
characters = np.char.upper(characters)
# Result: array([' ', 'H', 'E', 'L', 'L', 'O', ' ', ' ', 
#           'W', 'O', 'R', 'L', 'D', ' ', ' '], dtype='<U1')

### Split the words by finding the indices of spaces
sp = np.where(characters == ' ')
# Result: (array([ 0, 6, 7, 13, 14], dtype=int64),)
# A little trick: let's double each index, and then take pairs
sp2 = np.repeat(sp, 2)
# Result: array([ 0, 0, 6, 6, 7, 7, 13, 13, 14, 14], dtype=int64)
# Get the pairs as a 2D matrix, skip the first and the last
w_ranges = np.reshape(sp2[1:-1], (-1, 2))
# Result: array([[ 0,  6],
#                [ 6,  7],
#                [ 7, 13],
#                [13, 14]], dtype=int64)
# Remove the indexing to the spaces themselves
w_ranges = w_ranges[np.where(w_ranges[:, 1] - w_ranges[:, 0] > 2)]
# Result: array([[ 0,  6],
#                [ 7, 13]], dtype=int64)

# Voila! Words are in between spaces, given as pairs of indices
words = list(map(lambda r: characters[r[0]:r[1]], w_ranges))
# Result: [array([' ', 'H', 'E', 'L', 'L', 'O'], dtype='<U1'), 
#          array([' ', 'W', 'O', 'R', 'L', 'D'], dtype='<U1')]
# Let's recode the characters as strings
swords = np.array(list(map(lambda w: ''.join(w).strip(), words)))
# Result: array(['HELLO', 'WORLD'], dtype='<U5')

# Next, let's remove stop words
stop_words = np.array(list(set(open('../stop_words.txt').read().split(','))))
#Turn the stop words into upper case as well
stop_words = np.char.upper(stop_words)
ns_words = swords[~np.isin(swords, stop_words)]


#----------Leet Implementation----------------
leet = '4BCD3FGH1JKLMN0PQRSTµVWXYZ'
#map of vowels and their leet counterparts
leet_map = str.maketrans(string.ascii_uppercase , leet)
#replaces the vowels with their leet counterparts
leet_chars = np.char.translate(ns_words, leet_map)

#use the same method as above to do the 2-gram
index = np.repeat(leet_chars,2)
words_two_grams = np.reshape(index[1:-1], (-1, 2))


### Finally, count the word occurrences
uniq, counts = np.unique(words_two_grams, axis=0, return_counts=True)
wf_sorted = sorted(zip(uniq, counts), key=lambda t: t[1], reverse=True)

for two_grams, c in wf_sorted[:25]:
    print(two_grams, '-', c)

