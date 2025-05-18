# ===== Makefile =====

# Direktori
SRC := src
BIN := bin

# Package subfolder
# ALGO := $(SRC)/algoritma/*.java
MAIN := $(SRC)/main/*.java
MODEL := $(SRC)/model/*.java
UTILS := $(SRC)/utils/*.java

# Compile: all .java to bin/
build:
	@mkdir -p $(BIN)/main $(BIN)/model $(BIN)/utils
	javac -d $(BIN) $(MAIN) $(MODEL) $(UTILS)

# Jalankan program
run: build
	java -cp $(BIN) main.Main

# Build saja
all: build

# Hapus hasil kompilasi
clean:
	rm -rf $(BIN)