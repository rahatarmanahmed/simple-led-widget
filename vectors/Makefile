# SVG to PNG resources for Android

# *** Commands
INKSCAPE = inkscape
EXPR = expr
MKDIR = @mkdir -p
RM = rm -f

# *** Flags
INKSCAPEFLAGS = -y 0.0

# *** Constants
LDPI = 120
MDPI = 160
HDPI = 240
XHDPI = 320

# *** User-changeable variables
OUTPUT_DIR := ../res
EXTRA_DIR_QUALS =

# All files to read, separated by space
# The default value will read all files in this folder ending with .svg
INPUT_FILES := $(wildcard appwidget_settings_led_*big.svg)

# If you want to explicitly tell it the original size, change these
# The size is assumed as being the one for MDPI, and the sizes for the other DPIs are calculated from it
# It does not necessarily have to match the size of the file itself; these
# could be seen as simply being what the size for MDPI should be
# or how big each file should be in dp
#FILES_WIDTH = $(shell $(INKSCAPE) -W $^)
#FILES_HEIGHT = $(shell $(INKSCAPE) -H $^)

FILES_WIDTH = 250
FILES_HEIGHT = 250

# *** Input files
LDPI_OUTPUTS := $(patsubst %.svg, $(OUTPUT_DIR)/drawable$(EXTRA_DIR_QUALS)-ldpi/%.png, $(INPUT_FILES))
MDPI_OUTPUTS := $(patsubst %.svg, $(OUTPUT_DIR)/drawable$(EXTRA_DIR_QUALS)-mdpi/%.png, $(INPUT_FILES))
HDPI_OUTPUTS := $(patsubst %.svg, $(OUTPUT_DIR)/drawable$(EXTRA_DIR_QUALS)-hdpi/%.png, $(INPUT_FILES))
XHDPI_OUTPUTS := $(patsubst %.svg, $(OUTPUT_DIR)/drawable$(EXTRA_DIR_QUALS)-xhdpi/%.png, $(INPUT_FILES))

all: ldpi mdpi hdpi xhdpi

clean: ldpi-clean mdpi-clean hdpi-clean xhdpi-clean

ldpi: $(LDPI_OUTPUTS)
mdpi: $(MDPI_OUTPUTS)
hdpi: $(HDPI_OUTPUTS)
xhdpi: $(XHDPI_OUTPUTS)

ldpi-clean:
	$(RM) $(LDPI_OUTPUTS)

mdpi-clean:
	$(RM) $(MDPI_OUTPUTS)

hdpi-clean:
	$(RM) $(HDPI_OUTPUTS)

xhdpi-clean:
	$(RM) $(XHDPI_OUTPUTS)

$(OUTPUT_DIR)/drawable$(EXTRA_DIR_QUALS)-ldpi/%.png: %.svg
	$(MKDIR) $(@D)
	$(eval $@_W := $(shell expr $(LDPI) "*" $(FILES_WIDTH) / $(MDPI)))
	$(eval $@_H := $(shell expr $(LDPI) "*" $(FILES_HEIGHT) / $(MDPI)))
	$(INKSCAPE) $(INKSCAPEFLAGS) -w $($@_W) -h $($@_H) -e $@ $<

$(OUTPUT_DIR)/drawable$(EXTRA_DIR_QUALS)-mdpi/%.png: %.svg
	$(MKDIR) $(@D)
	$(eval $@_W := $(FILES_WIDTH))
	$(eval $@_H := $(FILES_HEIGHT))
	$(INKSCAPE) $(INKSCAPEFLAGS) -w $($@_W) -h $($@_H) -e $@ $<

$(OUTPUT_DIR)/drawable$(EXTRA_DIR_QUALS)-hdpi/%.png: %.svg
	$(MKDIR) $(@D)
	$(eval $@_W := $(shell expr $(HDPI) "*" $(FILES_WIDTH) / $(MDPI)))
	$(eval $@_H := $(shell expr $(HDPI) "*" $(FILES_HEIGHT) / $(MDPI)))
	$(INKSCAPE) $(INKSCAPEFLAGS) -w $($@_W) -h $($@_H) -e $@ $<

$(OUTPUT_DIR)/drawable$(EXTRA_DIR_QUALS)-xhdpi/%.png: %.svg
	$(MKDIR) $(@D)
	$(eval $@_W := $(shell expr $(XHDPI) "*" $(FILES_WIDTH) / $(MDPI)))
	$(eval $@_H := $(shell expr $(XHDPI) "*" $(FILES_HEIGHT) / $(MDPI)))
	$(INKSCAPE) $(INKSCAPEFLAGS) -w $($@_W) -h $($@_H) -e $@ $<