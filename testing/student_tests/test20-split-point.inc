I single_commit_setup.inc
I definitions.inc
> log
===
${HEADER}
${DATE}
a is a wug

===
${HEADER}
${DATE}
initial commit
<<<*
D UID2 "${1}"
D UID1 "${2}"

> branch other
<<<

> checkout other
<<<

+ d.txt wug.txt

> add d.txt
<<<

> commit "added d.txt to other"
<<<

+ b.txt notwug.txt

> add b.txt
<<<

> commit "added b.txt to other"
<<<

> checkout master
<<<

+ c.txt wug.txt

> add c.txt
<<<

> commit "added c.txt to master"
<<<

> split other
${UID2}
<<<


