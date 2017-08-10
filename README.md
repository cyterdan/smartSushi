# smartSushi
Outsmart your sushi provider

## Why ?

Some sushi restaurants (particularly in France) have menus that are hard to optimise when you order as a group.
Let's say you and your friends want to order sushis. Each person looks at the menu and chooses one or more menu items, 
depending on what he want's to eat. 
This is inefficient, as each person may end up ordering things that he did not wanted.

For example, a simplified menu has the following items : 
- M1 : 4xSashimi, 3x Sushi , 1 salad for 9€
- M2 : 10xSashimi for 12€
- M3 : 8xSushi for 10€
- M4 : 1xSalad for 2€

3 persons (A,B,C) are ordering. 
A wants 3 Sashimi and 3 Sushi
B Wants 8 Sushi and 1 salad
C Wants 5 Sashimi and 1 salad 

If each person was ordering alone : 
- A would order 1xM1 for 9€
- B would order 1xM3 for 10€
- C would order 1xM2 + 1xM4 for 14€
total = 9+10+14 = 33€

If we aggregate the requirements, we would require : 
8 sashimi
11 sushi
2 salads

We can order : 
M1x2 + M3 
total = 2x9 + 10 = 28€

## Usage 
At this point, you can only use it in java (https://github.com/cyterdan/smartSushi/blob/master/backend/src/main/java/xyz/cyterdan/smartsushi/JapaneseMenu.java)

A web ui is in progress

## Adding a menu

here's an example of a menu for a restaurant called Itouya
https://docs.google.com/spreadsheets/d/1W9ZqwvalNYdbUkLfRv98bq21lGLqdjUoUkTLjPQBDyM/edit#gid=0

to add a menu, clone and modify this worksheet
