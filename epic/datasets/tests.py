from django.test import TestCase

from epic.datasets.models import DataSet

class IndexTestCase(TestCase):
	fixtures = ['initial_data', 'single_dataset', 'initial_users']
	
	def setUp(self):
		self.data_set = DataSet.objects.all()[0]
	
	def testIndex(self):
		response = self.client.get('/datasets/')
		self.failUnlessEqual(response.status_code, 200, "Error listing datasets!")
		data = response.context[0]['datasets'] #due to template inheritance
		self.failUnlessEqual(len(data), 1, "We don't have as many datasets as we expect!")
		self.failUnlessEqual(data[0].name, "Important Data", "The default data we loaded is named incorrectly!")
		self.failUnless("Important Data" in response.content, "Data set names aren't displayed on the list page!")
	
	def testRatingView(self):
		response = self.client.get('/datasets/')
		self.failUnlessEqual(response.status_code, 200, "Error listing datasets!")
		self.failUnless("--" in response.content, "There should be unrated data")
		response = self.client.post("/login/", {'username': 'bob', 'password':'bob2',})
		response = self.client.get('/datasets/')
		self.failUnlessEqual(response.status_code, 200, "Error listing datasets!")
		self.failUnless("--" in response.content, "There should be unrated data")
	
	def testRating(self):
		from django.test.client import Client
		#TODO remove once we use a version that is past r9847
		# http://code.djangoproject.com/changeset/9847
		# http://code.djangoproject.com/ticket/8551
		c = Client(REMOTE_ADDR='127.0.0.1')
		# Make sure there is no rating
		dataset_location = '/datasets/%s/' % (self.data_set.id)
		response = c.get(dataset_location)
		self.failIf("Rating:" in response.content)
		# Rate
		rate_location = dataset_location + "rate/3/"
		response = c.get(rate_location)
		self.failUnlessEqual(response.status_code, 302)
		# Make sure there still no rating because anon people can't rate
		dataset_location = '/datasets/%s/' % (self.data_set.id)
		response = c.get(dataset_location)
		self.failIf("Rating:" in response.content)
		# Log in
		login = c.login(username='bob', password='bob')
		self.failUnless(login, 'Could not login')
		# Rate
		rate_location = dataset_location + "rate/3/"
		response = c.get(rate_location)
		self.failUnlessEqual(response.status_code, 302)
		# Make sure bob's rating counted
		dataset_location = '/datasets/%s/' % (self.data_set.id)
		response = c.get(dataset_location)
		self.failUnless("Rating:" in response.content)
		self.failUnless("3" in response.content)
		# Log in as bob2
		login = c.login(username='bob2', password='bob2')
		self.failUnless(login, 'Could not login')
		# Rate
		rate_location = dataset_location + "rate/5/"
		response = c.get(rate_location)
		self.failUnlessEqual(response.status_code, 302)
		# Make sure bob2's rating counted
		dataset_location = '/datasets/%s/' % (self.data_set.id)
		response = c.get(dataset_location)
		self.failUnless("Rating:" in response.content)
		self.failUnless("4" in response.content)
		# Rate AGIAN!
		rate_location = dataset_location + "rate/5/"
		response = c.get(rate_location)
		self.failUnlessEqual(response.status_code, 302)
		# Make sure bob2's second rating didn't count
		dataset_location = '/datasets/%s/' % (self.data_set.id)
		response = c.get(dataset_location)
		self.failUnless("Rating:" in response.content)
		self.failUnless("4" in response.content)