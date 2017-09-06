import praw
import pdb
import re
import os


# Create the Reddit instance
reddit = praw.Reddit('bot1')

# and login
#reddit.login("baninanana", "BaNina199735")

# Have we run this code before? If not, create an empty list
if not os.path.isfile("posts_replied_to.txt"):
    posts_replied_to = []

# If we have run the code before, load the list of posts we have replied to
else:
    # Read the file into a list and remove any empty values
    with open("posts_replied_to.txt", "r") as f:
        posts_replied_to = f.read()
        posts_replied_to = posts_replied_to.split("\n")
        posts_replied_to = list(filter(None, posts_replied_to))

# Now do the same with comments
if not os.path.isfile("comments_replied_to.txt"):
    comments_replied_to = []

else:
    with open("comments_replied_to.txt", "r") as f:
        comments_replied_to = f.read()
        comments_replied_to = comments_replied_to.split("\n")
        comments_replied_to = list(filter(None, comments_replied_to))

potatoes = ["Is that even a potato?", "Yams are also a type of potato!", "That's an average looking potato", "Potatoes can be poisonous!", "That is a potato. A very dangerous potato. Do not engage."]


while (True):
# Get the top 5 values from our subreddit
    subreddit = reddit.subreddit('pythonforengineers')
    for submission in subreddit.new(limit=10):
        print(submission.title)

        submission.comments.replace_more(limit=0)

        for comment in submission.comments.list():
            if comment.id not in comments_replied_to:

        # Do a case insensitive search
                for x in range(0,5):
                    if re.search("Potato Bot, this potato is a " + str(x), comment.body, re.IGNORECASE):
            # Reply to the post
                        comment.reply(str(potatoes[x]))
                        print("Bot replying to : ", comment.body)

            # Store the current id into our list
                        comments_replied_to.append(comment.id)
        

    # If we haven't replied to this post before
        if submission.id not in posts_replied_to:

        # Do a case insensitive search
            for x in range(0,6):
                if re.search("Potato Bot, this potato is a " + str(x), submission.title, re.IGNORECASE):
            # Reply to the post
                    submission.reply(str(potatoes[x]))
                    print("Bot replying to : ", submission.title)

            # Store the current id into our list
                    posts_replied_to.append(submission.id)

# Write our updated list back to the file
    with open("posts_replied_to.txt", "w") as f:
        for post_id in posts_replied_to:
            f.write(post_id + "\n")

    with open("comments_replied_to.txt", "w") as f:
        for comment_id in comments_replied_to:
            f.write(comment_id + "\n")