# GPLP

Program for optimize paths availabilities and their cost in graphs. For this it create and solve linnear programming models.
For run you need to have LPSolve C-library and its Java's language interface wrapper. 

LPSolve library installation Linux
Add to /home/USER/.bashrc the follow lines:

LD_LIBRARY_PATH=/usr/local/lib/lp_solve_5.5.2.3_dev_ux64
export LD_LIBRARY_PATH

where /usr/local/lib/lp_solve_5.5.2.3_dev_ux64 is the path to library lpsolve55j.so
