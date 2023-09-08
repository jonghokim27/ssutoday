from imports import *
from db import *

from subroutine.website import *


def start():
    try:
        print("[INFO] Starting Routine")
        
        print("[INFO] Starting Website Subroutine")
        # Website Subroutine
        website(DB)

        print("[INFO] Ending Routine")

    except Exception as e:
        print("Error (routine.py) : "+str(e))
