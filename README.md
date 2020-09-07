# PoshMark

Scenario 1:

In case of scenaio, where the hours and CPU count is given ,  the combination of CPUs equal to the expected CPU count is obtained for each region.

Using the combination , the price of each combo is calculated and stored in a list.

This list is sorted based on price in ascending order.

The top most element contains the stack with least price.

Same operation is done for all regions i.e Asia, USE, USW.

We will be getting 1 least priced stack from each region summing to 3 stacks which is again sorted by price.

Out of the 3 stacks, the top most stack with least price is printed as output.


Scenario 2:

In case of scenaio, where the hours and price is given, the price per hour is calculated.  

The stacks for each region is iterated using a loop.

The price of each CPU size in each stack is used to obtain the maximum count of CPU per size.

The remiaining cost is compared with prices of different CPU size.

For each stack, the price and the CPU count is stored.

The stacks from all 3 regions are stored in a list which is sorted by the CPU count in descending order.

The stack with max CPU count is printed as output.



Scenario 3:

In case of scenaio, where the hours,CPU count and price is given,  the combination of CPUs equal to the expected CPU count is obtained for each region.

Using the combination , the price of each combo is calculated and stored in a list.

The list is then filtered out based on the price expected by the user.

The resultant list is printed as output.



