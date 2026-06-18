import tkinter as tk
import xlrd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as anime 
import mpl_toolkits.mplot3d.axes3d as p3
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import time
r = open("solar_map_pos.txt","r")
rr = open("earth_map_pos.txt","r")

#fig = plt.figure()
#sub = p3.Axes3D(fig)

fig, sub = plt.subplots(1,2)

sub[0].set_xlabel('X Label')
sub[0].set_ylabel('Y Label')
#sub[0].set_zlabel('Z Label')
sub[1].set_xlabel('X Label')
sub[1].set_ylabel('Y Label')
#sub[1].set_zlabel('Z Label')

start = []
start2 = []
a = r.readline()
a = r.readline()
aa = rr.readline()
aa = rr.readline()
while a[0] != "*":
    if(a[0] != "*"):
        start.append(a.split(","))
    a = r.readline()
while aa[0] != "*":
    if(aa[0] != "*"):
        start2.append(aa.split(","))
    aa = rr.readline()


plots = []
plots2 = []

sc = 7
ssc = 0
n = 0
colours = ["y","b","r","grey"]
for i in start:
    plots.append(sub[0].plot(np.divide(float(i[0]),10**sc),np.divide(float(i[1]),10**sc), c=colours[n], marker ="o"))
    n += 1
n = 0
for i in start2:
    plots2.append(sub[1].plot(np.divide(float(i[0]),10**ssc),np.divide(float(i[1]),10**ssc), c=colours[n], marker ="o"))
    n += 1

s = 100
sub[0].set(xlim=[-s, s], ylim=[-s, s])
s = 1000
sub[1].set(xlim=[-s, s], ylim=[-s, s])
for i in range(len(plots)):
    plots[i] = plots[i][0]
for i in range(len(plots2)):
    plots2[i] = plots2[i][0]
    
def update(frame):

    a = r.readline()
    aa = rr.readline()
    
    if(aa[0] == None):
        return(plots,plots2)
    
    for i in range(60*60):
        for i in range(len(plots)+1):
            r.readline()
    for i in range(60*60):
        for i in range(len(plots2)+1):
            rr.readline()

    if(a[0] == "*"):
        a = r.readline()
    if(aa[0] == "*"):
        aa = rr.readline()
        
    new = []
    new2 = []
    for i in range(len(plots)):
        if(a[0] == "*"):
            a = r.readline()
        new.append(a.split(","))
        a = r.readline()
        
    for i in range(len(plots2)):
        if(aa[0] == "*"):
            aa = rr.readline()
        new2.append(aa.split(","))
        aa = rr.readline()

    n = 0
    for i in plots:
        i.set_data([np.divide(float(new[n][0]),10**sc),np.divide(float(new[n][1]),10**sc)])
        n += 1
    n = 0
    for i in plots2:
        i.set_data([np.divide(float(new2[n][0]),10**ssc),np.divide(float(new2[n][1]),10**ssc)])
        n += 1
    return (plots,plots2)


ani = anime.FuncAnimation(fig=fig, func=update, frames=365*24*60*60, interval=1,repeat=True)
writer = anime.PillowWriter(fps=30)
#ani.save("output.gif", writer = writer)