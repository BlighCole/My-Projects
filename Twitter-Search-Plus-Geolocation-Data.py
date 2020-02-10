# -*- coding: UTF-8 -*-
import twitter
import datetime
import googlemaps


# ----------------------------------------------------------
# --------          HW 12: Twitter Search          ---------
# --------          Plus Geolocation Data          ---------
# ----------------------------------------------------------

# Code by Cole Bligh
# Note: This file only contains the code I created, none of the code that
#   was provided to allow it to function

consumer_key = 
consumer_secret = 
geocode_api_key = 

test_screen_name = 'colgateuniv'
test_address = '1600 Amphitheatre Parkway, Mountain View, CA'




# ----------------------------------------------------------
# Main Function for Testing
# ----------------------------------------------------------
def main():
    
    screen_name = 'colgateuniv'
    lat = '42.8166'
    long = '-75.5402'
    
    twitter_api = get_twitter_api()
#    test_twitter_connection()
    gmaps_client = get_gmaps_client()
#    test_gmaps_connection()
    search(twitter_api, gmaps_client)

##    user = get_user(twitter_api, screen_name)
##    print(user)
##    info = user_to_string(get_tweet(twitter_api, screen_name))
##    print(info)
##    tweet = get_tweets_by_user(twitter_api, screen_name)
##    print(tweet)
##    tweet2 = get_tweets_by_keyword(twitter_api, 'Colgate')
##    print(tweet2)
##    topics = get_trending_topics(twitter_api)
##    print(topics)
##    print(tweet_to_string(get_tweet(twitter_api, screen_name)))
#    print(get_trending_users(twitter_api))
#    print(get_trending_tweets(twitter_api))
    
    

    
##    addr = get_addr_from_cood(gmaps_client, lat, long)
##    print(addr)
##    lat, long = get_cood_from_addr(gmaps_client, addr)
##    print('(' + str(lat) + ', ' + str(long) + ')')
##    name = get_place_name_from_cood(gmaps_client, long, lat)
##    name = get_place_name_from_addr(gmaps_client, addr)
##    print(name)
#    print(get_tweets_near_location(twitter_api, lat, long, '1mi'))
#    print(get_tweet_place_name(twitter_api, gmaps_client))


    
# ----------------------------------------------------------
# Write Your Code Here
# ----------------------------------------------------------

def tweet_to_string( status, location=False ):
    ''' updated version of the code written in Lecture 25 '''
    try:
        user = status.user.screen_name
        date = tweet_date(status.created_at)
        tweet = status.text.replace("\n"," ")
        s = 'Tweet from @' + user + date + ': "' + tweet + '"'
        if location: # optionally append location name
            s += ' (' + status.place['full_name'] + ')'
        return s
    except twitter.error.TwitterError as e:
        print('Error in status_to_string: ' + e + '\n')
        return False
    
def user_to_string(user):
    '''takes a tweet from a user as a parameter and returns a string with the
        couunt screen name, the account name, and the account description'''
    account_screen_name = user.user.screen_name
    account_name = user.user.name
    account_description = user.user.description
    return '@' + account_screen_name + ' (' + account_name + ') "' + account_description + '"'


def get_user(api, screen_name):
    '''takes the twitter API and a screen name as parameters and returns the
        the screen name of the user'''
    tweet = get_tweet(api, screen_name)
    screen_name = tweet.user.screen_name
    return screen_name

def get_tweets_by_user(api, screen_name):
    '''takes the twitter API and a screen name as parameters and returns the
        the tweets in a list by the user'''
    timeline = api.GetUserTimeline(screen_name = screen_name)
    tweets = []
    for time in timeline:
        tweets.append(time.text)
    return tweets

def get_tweets_by_keyword(api, keyword):
    '''takes the twitter API and a keyword as parameters and returns the tweets
        in a list that contain the keyword'''
    timeline = api.GetSearch(keyword, return_json = True)['statuses']
    tweets = []
    for time in timeline:
        tweets.append(time['text'])
    return tweets

def get_trending_topics(api):
    '''takes the twitter API as a parameter and returns the trending topics
        in the form of a list'''
    trending = api.GetTrendsCurrent()
    topics = []
    for Trend in trending:
        topics.append(Trend.name)
    return topics

def get_trending_tweets(api):
    '''takes the twitter API as a parameter and returns the trending tweets
        in the form of a list of lists'''
    trends = get_trending_topics(api)
    tweets = []
    for trend in trends:
        tweets.append(get_tweets_by_keyword(api, trend))
    return tweets

def get_trending_users(api):
    '''takes the twitter API as a parameter and returns a dictionary with
        the trend as the key and a list of the users that have tweeted about it
        as the object'''
    trends = get_trending_topics(api)
    trending = {}
    for trend in trends:
        users = api.GetUsersSearch(term=trend)
        trending[trend] = users
    return trending




# ----------------------------------------------------------
# New Location Functions

def get_addr_from_cood(client, long, lat):
    '''takes the google maps client, the latitude, and longitude as the
        parameters and returns the address of the location'''
    location = client.reverse_geocode([long, lat])[0]['formatted_address']
    return location

def get_cood_from_addr(client, address):
    '''takes the google maps client and the address of the location and returns
        the latitude and longitude'''
    geo = client.geocode(address)[0]['geometry']['location']
    return geo['lng'], geo['lat']


def get_place_name_from_cood(client, ref_long, ref_lat):
    '''takes the google maps client, the latitude, and longitude as parameters
        and returns the name of the place'''
    place = input('Type of place near your to search for: ')
    name = client.places(query = place, location = [ref_long, ref_lat])['results']

    #checks to save name of closest place of interest to return
    if len(name) == 0:
        output = 'No ' + place + ' near you'
        return output
    long_closest = float(name[0]['geometry']['location']['lng'])
    lat_closest = float(name[0]['geometry']['location']['lat'])

    place = name[0]['name']
    for i in range(len(name) - 1):
        if find_closest(name[i + 1], lat_closest, long_closest, ref_lat, ref_long) == True:
            long_closest, lat_closest, place = update(name[i + 1])
    return place

def get_place_name_from_addr(client, address):
    '''takes the google maps client and address as parameters and returns the
        name of the place'''
    lat, long = get_cood_from_addr(client, address)
    place = get_place_name_from_cood(client, long, lat)
    return place

def get_tweet_place_name(api, tweet):
    '''takes the twitter API and a tweet as parameters and returns the name
        of the place the tweet is from'''
    timeline = api.GetSearch('Colgate', return_json = True)['statuses']
 #   long, lat = get coordinates of the tweet
    place = get_place_name_from_cood(maps_client, long, lat)
    return place

def get_tweets_near_location(api, lat, long, radius):
    '''takse the twitterAPI, longtitude, latitude, and the search radius as
        parameters and returns the tweets from within this area'''
    geocode = long + ',' + lat + ',' + radius
    timeline = api.GetSearch(geocode=geocode)
    tweets = []
    for i in range(len(timeline)):
        tweets.append(timeline[i].text)
    return tweets


# ----------------------------------------------------------
# Search Function

def search(twitter_api, gmaps_client):
    ''' primary function to be written '''
    print('WELCOME!!')
    cood, inputs = give_cood_or_addr()
    if cood == True:
        print('Welcome to the Coordinates Search Screen!!')
        decision = cood_prompt()
        while decision != 6:
            cood_actions(decision, inputs, twitter_api, gmaps_client)
            print('Coordinates Search Screen')
            decision = cood_prompt()
    else:
        print('Welcome to the Address Search Screen!!')
        decision = addr_prompt()
        while decision != 6:
            addr_actions(decision, inputs, twitter_api, gmaps_client)
            print('Address Search Screen')
            decision = addr_prompt()
    print('Goodbye!')
    return None





# ----------------------------------------------------------
# Write Any Helper Functions Here
# ----------------------------------------------------------

def tweet_date( date_str ):
    try:
        date = datetime.datetime.strptime(date_str,
                                          '%a %b %d %H:%M:%S %z %Y')
        return date.strftime(' at %I:%M %p on %a %d, %Y')
    except Exception as e:
        print('Error in get_tweet_data: ' + e)
        return ''

def get_tweet(api, screen_name):
    '''takes the twitter API and the screen name as parameters and returns
        most recent tweet from that user'''
    timeline = api.GetUserTimeline(screen_name = 'colgateuniv')
    return timeline[0]

def find_closest(name, lat_closest, long_closest, ref_lat, ref_long):
    '''compares the latitude and longitude of the closest point and the
        reference point with coordinates of another place
        returns True: the new place is closer than the current closest
        returns False: if the current closest place is closer'''
    longitude = float(name['geometry']['location']['lng'])
    latitude = float(name['geometry']['location']['lat'])
    dist2 = ((longitude - float(ref_long)) ** 2 + (latitude - float(ref_lat)) ** 2) ** 0.5
    dist1 = ((lat_closest - float(ref_long)) ** 2 + (long_closest - float(ref_lat)) ** 2) ** 0.5
    if dist1 > dist2:
        return True
    return False

def update(name):
    '''updates the latitude, longitude, and place variables and prints them out'''
    long = float(name['geometry']['location']['lng'])
    lat = float(name['geometry']['location']['lat'])
    place = name['name']
    return long, lat, place


def give_cood_or_addr():
    '''requests from the user if they would like to input their current
        address or coordinates and then has them enter them and they are
        returned, also True is returned if the user inputted coordinates and
        False if the address was inputted'''
    choice = input('Would you like to give coordinates or address as an input? ')
    while choice.lower() != 'coordinates' and choice.lower() != 'address':
        choice = input('Enter "coordinates" or "address": ')
        print('\n')
    if choice.lower() == 'coordinates':
        lat = input('What is the latitude of your location? ')
        long = input('What is the longitude of your location? ')
        print('\n\n')
        return True, [long, lat]
    else:
        addr = input('What is the address of your location? ')
        print('\n\n')
        return False, addr

def cood_prompt():
    '''prompts the user of what they are able to do from the current screen and
        returns their choice'''
    print('1) Get Your Address')
    print('2) Get the Name of Closest Point of Interest')
    print('3) Get Trending Topics')
    print('4) Get Most Recent Tweet From User')
    print('5) Get User Info')
    print('6) Quit')
    print()
    choice = input('What would you like to do? ')
    while choice != '1' and choice != '2' and choice != '3' and choice != '4' and choice != '5' and choice != '6':
        choice = input('Enter an integer from 1 to 6: ')
    print('\n\n')
    return int(choice)

def addr_prompt():
    '''prompts the user of what they are able to do from the current screen and
        returns their choice'''
    print('1) Get Your Coordinates')
    print('2) Get the Name of Closest Point of Interest')
    print('3) Get Trending Topics')
    print('4) Get Most Recent Tweet From User')
    print('5) Get User Info')
    print('6) Quit')
    print()
    choice = input('What would you like to do? ')
    while choice != '1' and choice != '2' and choice != '3' and choice != '4' and choice != '5' and choice != '6':
        choice = input('Enter an integer from 1 to 6: ')
    print('\n\n')
    return int(choice)

def cood_actions(decision, inputs, twitter_api, gmaps_client):
    '''takes a parameter of the integer option the user wants to use and does
        the requested action, returns None'''
    if decision == 1:
        print('Your Address is:')
        print(get_addr_from_cood(gmaps_client, inputs[0], inputs[1]))
        print('\n\n')
    elif decision == 2:
        place = get_place_name_from_addr(gmaps_client, inputs)
        print('Closest Point of Interest:')
        print(place)
        print('\n\n')
    elif decision == 3:
        trends = get_trending_topics(twitter_api)
        print('Trending Topics:')
        if len(trends) <= 10:
            for trend in trends:
                print(trend)
        else:
            for i in range(10):
                print(trends[i])
        print('\n\n')
    elif decision == 4:
        screen_name = input('What is the screen name for the user? ')
        print()
        print(tweet_to_string(get_tweet(twitter_api, screen_name)))
        print('\n\n')
    elif decision == 5:
        screen_name = input('What is the screen name for the user? ')
        print()
        print(user_to_string(get_tweet(twitter_api, screen_name)))
        print('\n\n')
    return None

def addr_actions(decision, inputs, twitter_api, gmaps_client):
    '''takes a parameter of the integer option the user wants to use and does
        the requested action, returns None'''
    if decision == 1:
        print('Your Coordinates:')
        print(get_cood_from_addr(gmaps_client, inputs))
        print('\n\n')
    elif decision == 2:
        place = get_place_name_from_addr(gmaps_client, inputs)
        print('Closest Point of Interest:')
        print(place)
        print('\n\n')
    elif decision == 3:
        trends = get_trending_topics(twitter_api)
        print('Trending Topics:')
        if len(trends) <= 10:
            for trend in trends:
                print(trend)
        else:
            for i in range(10):
                print(trends[i])
        print('\n\n')
    elif decision == 4:
        screen_name = input('What is the screen name for the user? ')
        print()
        print(tweet_to_string(get_tweet(twitter_api, screen_name)))
        print('\n\n')
    elif decision == 5:
        screen_name = input('What is the screen name for the user? ')
        print()
        print(user_to_string(get_tweet(twitter_api, screen_name)))
        print('\n\n')
    return None


main()
