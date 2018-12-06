# Prototype Evaluation Form

# Evaluator 1

## Feature Request

Title: UI theme

Detailed Description (minimum one paragraph):

It would be nice if the UI was either consistently a dark-theme background or consistently a light-theme background (maybe an option to choose whether they want a light or dark theme would be neat). The dark theme is nice, another suggestion would be to maybe have a different colour for the logout and connection buttons, or something so that when you scroll down the server list items don't blend into the buttons. 

## Bug Fix

Detailed Description (minimum one paragraph):

Time in set event --> saves as 24 hour clock, but if you enter a 24 hour clock time says invalid
If you save an event without a time, displays "Event saved November 29 at"
Always saves event on today's date.
Clicking "back" logs you out

# Evaluator 2

	## Feature Request
Server Manager Activity
	* “Create connection” and “logout” are hard to read (black text on dark blue background). They should be in a more readable color.
General
	* The user should be able to navigate through the app without having to use the android back button.
Invite Activity
	* There is no way for the user to see who has been invited.
Login Activity
	* The username is case-insensitive
	* The credentials are not removed when the user fails to login.

	# Bugs
Server Manager Activity
	* If the name of the server that the user specifies is too long, it overlaps the “access” button.
	* You can specify the same server more than once.
Calendar Activity
	* When you change the time, it defaults to the first value that was entered, not the current value.# Prototype Evaluation Form

# Evaluator 3

## Feature Request

Title: Whitelisting or blacklisting users

Detailed Description (minimum one paragraph):

It would be nice if server admins and owners could create whitelists or blacklists
in order to secure and manage their server. The invite system seems to be a bit weird and
I think it could be replaced with somethign more along the lines of user management.
"Inviting" individual users on little tiles seems weird. Especially can't uninvite them.

As well, the visual design of the ServerManagerActivity could be improved. Hard to differentiate
the scroll list from everythign else. Don't know where it begins or ends.

## Bug Fix

Detailed Description (minimum one paragraph):

The username for logging in is case-insensitive. I can login using the usernames
"JJ", "Jj", "jj", "jJ" with the password "Password123", and they all work. Once logged in, 
it says "Welcome, JJ", even if i logged in with jJ. Same thing for Nate, NAte, nAte, etc.

If I create a server as JJ, and invite Nate, Nate has no admin priviliges, but if Nate
creates another connection with the same server address, he can then invite Alex but not
JJ. It seems to think the entry is a separate server, but uses the same users set.

# Evaluator 4

## Feature Request

Title: Invite Users

Detailed Description (minimum one paragraph):

--> Show a list of invited users in server details
--> Have the ability to uninvite users

## Bug Fix

Detailed Description (minimum one paragraph):

Creating a connection in ServerManagerActivity:
	--> Can create multiple connections with identical names
	--> Can create connections with empty string or null
	--> Creating a long multiline domain will either make UI buttons to create or cancel
		unreachable or make UI controls on the list page of connections overlap text
		
	
