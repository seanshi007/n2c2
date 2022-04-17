# Reproduce `Evaluating shallow and deep learning strategies for the 2018 n2c2 shared task on clinical text classification`

This reproduction work is using Windows, however using Linux and MacOS will be easier (more convenient to build fastText). 

## Code Dependencies
- JDK8+
- python3 (to run official evaluation scripts)
- make (to compile fastText)
- gcc/clang (to compile fastText)

## Steps to reproduce the original work
- Download the annotated training and testing data from https://portal.dbmi.hms.harvard.edu/projects/n2c2-nlp/
- Copy all the train xml files (202 in total) to `data/train` directory, copy all test xml files (86 in total) to `data/test` folder
- To run the baseline, rule-based, and SVM classifier, simply run the `ClassifierRunner.java` and comment out other models in the `ClassifierFactory`
- To build fastText we need to install make
  - (Linux): `sudo apt install make`
  - (Windows): install Chocolately and then `choco install make`
- To build fastText we also need to install g++
  - (Linux): `sudo apt install g++`
  - (Windows): install MinGW-w64
- The `pom.xml` will download v0.2.0 of fastText, and we need to update the `Makefile` to use g++, and finally run `make` to get fastText executable
- Generate self-trained embeddings
  - run `SentenceDumper.java` to get all the sentences
  - run `train_embeddings.sh` to generate the skipgram model if on Linux or directly run corresponding fasttext.exe if on Windows
  - run `VocabularyDumper.java` to get all the words
  - run `print_vectors.sh` to get the word embeddings if on Linux or directly run corresponding fasttext.exe if on Windows
  - rename the generated `vectors.vec` to `self-trained-vectors.vec` and move it to `src/main/java/resources` 
- Generate pre-trained embeddings
  - this section needs to be performed on a 128 GB memory Linux machine (from one of the cloud providers)
  - `wget https://github.com/facebookresearch/fastText/archive/v0.2.0.zip`
  - `unzip v0.2.0.zip`
  - `cd fastText-0.2.0 && make`
  - `wget https://ftp.ncbi.nlm.nih.gov/pub/lu/Suppl/BioSentVec/BioWordVec_PubMed_MIMICIII_d200.bin`
  - copy `vocab.txt` generated from `VocabularyDumper.java` to the VM
  - run `print_vectors.sh` using the `BioWordVec_PubMed_MIMICIII_d200.bin` model
  - rename the generated `vectors.vec` to `BioWordVec-vectors.vec` and download it to `src/main/java/resources`
  - the VM can be stopped now
- Run `ClassifierRunner.java` to get the full result (including logistic regression and LSTM with pre-trained and self-trained embedding)
- The results are available in the `stats` folder

## Reference

The original [JAMIA paper](https://academic.oup.com/jamia/advance-article/doi/10.1093/jamia/ocz149/5568257):

```
@article{oleynik2019evaluating,
  title={Evaluating shallow and deep learning strategies for the 2018 n2c2 shared-task on clinical text classification},
  author={Michel Oleynik and Amila Kugic and Zdenko Kasáč and Markus Kreuzthaler},
  journal={Journal of the American Medical Informatics Association},
  publisher={Oxford University Press},
  year={2019}
}
```
