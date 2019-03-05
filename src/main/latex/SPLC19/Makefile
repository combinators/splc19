splc19.pdf: splc19.dvi
	dvips splc19
	pstopdf splc19.ps

FILES = conclusion.tex \
	design.tex \
	evaluation.tex \
	gpl.tex \
	introduction.tex \
	newApproach.tex

splc19.dvi: $(FILES) splc19.bib
	latex splc19
	bibtex splc19
	latex splc19


